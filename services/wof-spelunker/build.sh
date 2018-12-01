#!/bin/sh

BASE_DIR=`pwd`
if cd repos/whosonfirst-www-spelunker; then 
    git pull
    cd ${BASE_DIR}
else 
    git clone https://github.com/whosonfirst/whosonfirst-www-spelunker.git repos/whosonfirst-www-spelunker
fi

docker build -t lumberbarons/wof-spelunker:latest .