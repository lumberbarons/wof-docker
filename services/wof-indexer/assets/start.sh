#!/bin/sh

ELASTICSEARCH_URL="http://${ELASTICSEARCH_HOST}:9200"

cd /usr/local/indexer/es-schema
echo "Adding index to elasticsearch $ELASTICSEARCH_URL"
cat mappings.spelunker.json | curl -s -H "Content-Type: application/json" -X PUT ${ELASTICSEARCH_URL}/spelunker_20181120 -d @-
echo "\nAdding alias to elasticsearch"
curl -s -H "Content-Type: application/json" -X POST ${ELASTICSEARCH_URL}/_aliases -d '{ "actions": [ { "add": { "alias": "spelunker", "index": "spelunker_20181120" } } ] }'

echo "\n\nWaiting for files to index"

cd /usr/local/indexer

if [ ! -d /usr/local/data/bundles ]; then
    mkdir -p /usr/local/data/bundles
fi

inotifywait -q -m /usr/local/data/bundles -e modify |
    while read path action file; do
        echo "The file '$file' appeared in directory '$path' via '$action'"
        ./indexer.sh "${file}"
    done