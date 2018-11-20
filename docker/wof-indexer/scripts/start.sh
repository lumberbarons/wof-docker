#!/bin/sh

ELASTICSEARCH_URL="http://${ELASTICSEARCH_HOST}:9200"

ls /usr/local/data

cd /usr/local/data
if [ ! -d  /usr/local/data/whosonfirst-data ]; then
    echo "Downloading data repo"
    git clone ${WOFDATA_GITHUB_URL} whosonfirst-data
fi

cd /usr/local/mapzen
if [ ! -d /usr/local/mapzen/es-whosonfirst-schema ]; then
    echo "Downloading es schema"
    git clone https://github.com/lumberbarons/es-whosonfirst-schema.git
fi

if [ ! -f /usr/local/data/indexed ]; then
    datestamp=$(date "+%Y.%m.%d")
    index_name="spelunker_${datestamp}"

    cd /usr/local/mapzen/es-whosonfirst-schema
    cat schema/2.4/mappings.spelunker.json | curl -H "Content-Type: application/json" -X PUT ${ELASTICSEARCH_URL}/${index_name} -d @-
    curl -H "Content-Type: application/json" -X POST ${ELASTICSEARCH_URL}/_aliases -d "{ 'actions': [ { 'add': { 'alias': 'spelunker', 'index': '${index_name}' } } ] }"

    cd /usr/local/data/whosonfirst-data
    echo "Indexing data to: $ELASTICSEARCH_URL"
    wof-es-index -m directory . --index=spelunker -b --host=${ELASTICSEARCH_HOST}
    touch /usr/local/data/indexed
fi

echo "Indexing complete"