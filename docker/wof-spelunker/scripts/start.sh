#!/bin/sh -e

ROOT_DIR='/usr/local/mapzen'
REPO_DIR="$ROOT_DIR/whosonfirst-www-spelunker"

if [ ! -d ${REPO_DIR} ]; then
    cd ${ROOT_DIR}
    git clone https://github.com/lumberbarons/whosonfirst-www-spelunker.git
fi

echo "Configuring spelunker and gunicorn"
${REPO_DIR}/ubuntu/setup-spelunker.sh
${REPO_DIR}/ubuntu/setup-gunicorn.sh wof-elasticsearch

if [ ! -z ${NEXTZEN_KEY} ]; then
    echo "Setting nextzen api key in config"
    cd /usr/local/mapzen/whosonfirst-www-spelunker/www/static/javascript
    sed -i "s/nextzen-xxxxxx/${NEXTZEN_KEY}/g" mapzen.whosonfirst.config.js
fi

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