#!/bin/sh -e

ROOT_DIR='/usr/local/mapzen'
REPO_DIR="$ROOT_DIR/whosonfirst-www-spelunker"

if [ ! -d ${REPO_DIR} ]; then
    cd ${ROOT_DIR}
    git clone https://github.com/lumberbarons/whosonfirst-www-spelunker.git
fi

echo "Configuring gunicorn"
${REPO_DIR}/ubuntu/setup-gunicorn.sh wof-elasticsearch

echo "Configuring spelunker"
${REPO_DIR}/ubuntu/setup-spelunker.sh
if [ ! -z ${NEXTZEN_KEY} ]; then
    cd ${REPO_DIR}/www/static/javascript
    echo "Using nextzen api key: ${NEXTZEN_KEY}"
    sed -i "s/nextzen-xxxxxx/${NEXTZEN_KEY}/g" mapzen.whosonfirst.config.js
fi

# set data path in flask cfg
sed -i "s/https:\/\/data.whosonfirst.org/\/data/g" ${REPO_DIR}/config/whosonfirst-www-spelunker-flask.cfg

echo "Starting spelunker"

GUNICORN_APP_NAME='server'
GUNICORN_APP_ROOT="${REPO_DIR}/www"
GUNICORN_CONFIG="${REPO_DIR}/config/whosonfirst-www-spelunker-gunicorn.cfg"
GUNICORN_PID_NAME='whosonfirst-www-spelunker'

GUNICORN=`which gunicorn`
GUNICORN_OPTS="-c ${GUNICORN_CONFIG}"
GUNICORN_USER="www-data"

cd ${GUNICORN_APP_ROOT} 
echo "Starting ${GUNICORN_APP_NAME}"

sudo -u ${GUNICORN_USER} $GUNICORN $GUNICORN_OPTS ${GUNICORN_APP_NAME}:app