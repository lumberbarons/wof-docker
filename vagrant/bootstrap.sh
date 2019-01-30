#!/usr/bin/env bash

# create install directory
mkdir -p /usr/local/mapzen

# install docker
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
apt-get update
apt-cache policy docker-ce
apt-get install -y docker-ce

curl -L https://github.com/docker/compose/releases/download/1.23.2/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# install base packages
apt-get install -y git python-pip python-pyparsing python-setuptools libpython-dev libssl-dev libffi-dev

# install python packages
python -m pip install --upgrade --force pip
pip install --no-cache-dir --upgrade setuptools
pip install --no-cache-dir --upgrade pyopenssl ndg-httpsclient pyasn1 'requests[security]'

# install wof tools
cd /usr/local/mapzen
git clone https://github.com/whosonfirst/py-mapzen-whosonfirst.git
cd py-mapzen-whosonfirst
pip install --no-cache-dir --upgrade -r requirements.txt --process-dependency-links .

# start elasticsearch
mkdir -p /usr/local/mapzen/docker
cd /usr/local/mapzen/docker
cp /vagrant/assets/docker/docker-compose.yml  .
docker-compose up -d

# install spelunker
apt-get install -y gunicorn python-gevent gdal-bin
pip install --no-cache-dir flask flask-cors pycountry

cd /usr/local/mapzen
git clone https://github.com/whosonfirst/whosonfirst-www-spelunker.git

# setup spelunker
cd whosonfirst-www-spelunker
./ubuntu/setup-gunicorn.sh
./ubuntu/setup-spelunker.sh

SPLKR_DIR="/usr/local/mapzen/whosonfirst-www-spelunker"

FLASK_CFG="${SPLKR_DIR}/config/whosonfirst-www-spelunker-flask.cfg"
sed -i "s/data_root=.*/data_root=\/data/g" ${FLASK_CFG}
sed -i "s/static_root=.*/static_root=\/static/g" ${FLASK_CFG}

GUNICORN_CFG="${SPLKR_DIR}/config/whosonfirst-www-spelunker-gunicorn.cfg"
sed -i "s/spelunker_host =.*/spelunker_host = '0.0.0.0'/g" ${GUNICORN_CFG}

sed -i "s/nextzen-xxxxxx/${NEXTZEN_KEY}/g"  ${SPLKR_DIR}/www/static/javascript/mapzen.whosonfirst.config.js

killall gunicorn
/etc/init.d/whosonfirst-www-spelunker.sh start

# setup elasticsearch index for spelunker

cat /vagrant/assets/elasticsearch/mappings.spelunker.json | curl -X PUT http://localhost:9200/spelunker_20190101 -d @-
curl -X POST http://localhost:9200/_aliases -d '{ "actions": [ { "add": { "alias": "spelunker", "index": "spelunker_20190101" } } ] }'

# install nginx proxy

apt-get install -y nginx
cp /vagrant/assets/nginx/default.conf /etc/nginx/sites-available/default
service nginx restart

# install api

cd /usr/local/mapzen
git clone https://github.com/whosonfirst/whosonfirst-www-api.git

cd /usr/local/mapzen/whosonfirst-www-api

debconf-set-selections <<< "unattended-upgrades	unattended-upgrades/enable_auto_updates	boolean	true"

mysql_password=`date +%s | sha256sum | base64 | head -c 32`
debconf-set-selections <<< "mysql-server mysql-server/root_password password ${mysql_password}"
debconf-set-selections <<< "mysql-server mysql-server/root_password_again password ${mysql_password}"

echo "[mysql]" > ~/.my.cnf
echo "user=root" >> ~/.my.cnf
echo "password=${mysql_password}" >> ~/.my.cnf

sed -i "s/mysql -u root -p/mysql/g" ubuntu/setup-db.sh
sed -i "s/sudo dpkg-reconfigure/# sudo dpkg-reconfigure/g" ubuntu/setup-ubuntu.sh
export DEBIAN_FRONTEND=noninteractive

make setup-nossl

sed -i "s/80/8888/g" config/whosonfirst-www-api-apache.conf
echo "Listen 0.0.0.0:8888" > /etc/apache2/ports.conf
echo "Listen 0.0.0.0:9999" >> /etc/apache2/ports.conf

service apache2 restart

sed -i "s/You must set cfg.crypto_password_secret/$(date +%s | sha256sum | base64 | head -c 32)/g" www/include/secrets.php

sed -i '$ d' www/include/secrets.php
echo -e "\t\$GLOBALS['cfg']['github_oauth_key'] = '${GITHUB_OAUTH_KEY}';" >> www/include/secrets.php
echo -e "\t\$GLOBALS['cfg']['github_oauth_secret'] = '${GITHUB_OAUTH_SECRET}';\n" >> www/include/secrets.php
echo -e "\t#the end" >> www/include/secrets.php

echo "<?php" > www/include/config_local.php
echo -e "\t\$GLOBALS['cfg']['server_force_https'] = 0;" >> www/include/config_local.php
echo -e "\t\$GLOBALS['cfg']['api_require_ssl'] = 0;" >> www/include/config_local.php
echo -e "\t\$GLOBALS['cfg']['elasticsearch_spelunker_index'] = 'spelunker';" >> www/include/config_local.php

# index everything in the data directory
/vagrant/assets/scripts/index-all.sh /vagrant/data