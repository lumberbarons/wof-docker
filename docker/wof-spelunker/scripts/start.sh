#!/bin/sh -e

ROOT_DIR='/usr/local/mapzen'
SPELUNKER_DIR="$ROOT_DIR/whosonfirst-www-spelunker"

if [ ! -f ${ROOT_DIR}/configured ]; then
    echo "Configuring gunicorn"
    ${SPELUNKER_DIR}/ubuntu/setup-gunicorn.sh wof-elasticsearch

    echo "Configuring spelunker"
    ${SPELUNKER_DIR}/ubuntu/setup-spelunker.sh
    if [ ! -z ${NEXTZEN_KEY} ]; then
        cd ${SPELUNKER_DIR}/www/static/javascript
        sed -i "s/nextzen-xxxxxx/${NEXTZEN_KEY}/g" mapzen.whosonfirst.config.js
    fi

    # set data path in flask cfg
    sed -i "s/https:\/\/data.whosonfirst.org/\/data/g" ${SPELUNKER_DIR}/config/whosonfirst-www-spelunker-flask.cfg

    # skip configuration next time
    touch ${ROOT_DIR}/configured
fi

echo "Starting spelunker"

GUNICORN_APP_NAME='server'
GUNICORN_APP_ROOT="${SPELUNKER_DIR}/www"
GUNICORN_CONFIG="${SPELUNKER_DIR}/config/whosonfirst-www-spelunker-gunicorn.cfg"
GUNICORN_PID_NAME='whosonfirst-www-spelunker'

GUNICORN=`which gunicorn`
GUNICORN_OPTS="-c ${GUNICORN_CONFIG}"
GUNICORN_USER="www-data"

cd ${GUNICORN_APP_ROOT} 
echo "Starting ${GUNICORN_APP_NAME}"

sudo -u ${GUNICORN_USER} $GUNICORN $GUNICORN_OPTS ${GUNICORN_APP_NAME}:app