#!/bin/sh

FILE=${1}

ELASTICSEARCH_URL="http://${ELASTICSEARCH_HOST}:9200"

BUNDLES_DIR="/usr/local/data/bundles"
INDEXING_DIR="/usr/local/data/indexer"
DATA_DIR="/usr/local/data/whosonfirst-data"

if [ ! -f ${BUNDLES_DIR}/${FILE} ]; then
    echo "File ${FILE} does not exist, exiting"
    exit 1
fi

if [ ! -d ${INDEXING_DIR} ]; then
    mkdir -p ${INDEXING_DIR}
fi

echo "Indexing ${FILE}"

# get root directory of archive
ROOT_DIR=`tar -tjf ${BUNDLES_DIR}/${FILE} | sed -e 's@/.*@@' | uniq`

echo "Untaring ${FILE} to indexing directory"
tar -xjf ${BUNDLES_DIR}/${FILE} --directory ${INDEXING_DIR}

echo "Indexing data to $ELASTICSEARCH_URL"
wof-es-index -m directory ${INDEXING_DIR}/${ROOT_DIR} --index=spelunker -b --host=${ELASTICSEARCH_HOST}

if [ ! -d ${DATA_DIR} ]; then
    mkdir -p ${DATA_DIR}
fi

echo "Rsync to ${DATA_DIR}"
rsync -a ${INDEXING_DIR}/${ROOT_DIR}/ ${DATA_DIR}

echo "Deleting from bundle and indexing directories"
rm -rf ${BUNDLES_DIR}/${FILE}
rm -rf ${INDEXING_DIR}/${ROOT_DIR}

echo "Indexing of ${FILE} complete"