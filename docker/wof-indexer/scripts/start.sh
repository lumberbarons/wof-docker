#!/bin/sh

ELASTICSEARCH_URL="http://${ELASTICSEARCH_HOST}:9200"

cd /usr/local/mapzen
git clone https://github.com/lumberbarons/es-whosonfirst-schema.git

cd /usr/local/mapzen/es-whosonfirst-schema
cat schema/2.4/mappings.spelunker.json | curl -H "Content-Type: application/json" -X PUT ${ELASTICSEARCH_URL}/spelunker_20181117 -d @-
curl -H "Content-Type: application/json" -X POST ${ELASTICSEARCH_URL}/_aliases -d '{ "actions": [ { "add": { "alias": "spelunker", "index": "spelunker_20181117" } } ] }'

cd /usr/local/data/whosonfirst-data
echo "Indexing spelunker data to: $ELASTICSEARCH_URL"
wof-es-index -m directory . --index=spelunker -b --host=${ELASTICSEARCH_HOST}