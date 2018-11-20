#!/bin/sh

ELASTICSEARCH_URL="http://${ELASTICSEARCH_HOST}:9200"

if [ -f /usr/local/data/indexed ]; then
    echo "Indexing already completed, last commit in data repo:"
    cd /usr/local/data/whosonfirst-data
    git show --name-only
    exit 0
fi

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

cd /usr/local/mapzen/es-whosonfirst-schema
cat schema/2.4/mappings.spelunker.json | curl -H "Content-Type: application/json" -X PUT ${ELASTICSEARCH_URL}/spelunker -d @-

cd /usr/local/data/whosonfirst-data
echo "Indexing data to: $ELASTICSEARCH_URL"
wof-es-index -m directory . --index=spelunker -b --host=${ELASTICSEARCH_HOST}
touch /usr/local/data/indexed
echo "Indexing complete"