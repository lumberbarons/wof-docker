#!/bin/sh -e

ROOT_DIR="/usr/local/mapzen/whosonfirst-www-spelunker"

if [ ! -f ${ROOT_DIR}/configured ]; then
    # fake out script so it doesn't install service
    touch /etc/init.d/whosonfirst-www-spelunker.sh
    
    echo "Configuring gunicorn"
    ${ROOT_DIR}/ubuntu/setup-gunicorn.sh

    FLASK_CFG="${ROOT_DIR}/config/whosonfirst-www-spelunker-flask.cfg"
    sed -i "s/data_root=.*/data_root=\/data/g" ${FLASK_CFG}
    sed -i "s/host=.*/host=${ELASTICSEARCH_HOST}/g" ${FLASK_CFG}

    GUNICORN_CFG="${ROOT_DIR}/config/whosonfirst-www-spelunker-gunicorn.cfg"
    sed -i "s/spelunker_host =.*/spelunker_host = '0.0.0.0'/g" ${GUNICORN_CFG}

    echo "Configuring spelunker"
    ${ROOT_DIR}/ubuntu/setup-spelunker.sh
    if [ ! -z ${NEXTZEN_KEY} ]; then
        cd ${ROOT_DIR}/www/static/javascript
        sed -i "s/nextzen-xxxxxx/${NEXTZEN_KEY}/g" mapzen.whosonfirst.config.js
    fi

    # skip configuration next time
    touch ${ROOT_DIR}/configured
fi

GUNICORN_APP_NAME='server'
GUNICORN_APP_ROOT="${ROOT_DIR}/www"
GUNICORN_CONFIG="${ROOT_DIR}/config/whosonfirst-www-spelunker-gunicorn.cfg"
GUNICORN_PID_NAME='whosonfirst-www-spelunker'

GUNICORN=`which gunicorn`
GUNICORN_OPTS="-c ${GUNICORN_CONFIG}"
GUNICORN_USER="www-data"

cd ${GUNICORN_APP_ROOT} 
echo "Starting ${GUNICORN_APP_NAME}"

sudo -u ${GUNICORN_USER} $GUNICORN $GUNICORN_OPTS ${GUNICORN_APP_NAME}:app