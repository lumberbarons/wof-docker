FROM lumberbarons/wof-base:latest

# install deps
RUN apt-get update && apt-get install -y curl inotify-tools && rm -rf /var/lib/apt/lists/*

# slighty modified version of this https://github.com/whosonfirst/es-whosonfirst-schema/tree/master/schema/2.4
COPY assets/mappings.spelunker.json /usr/local/indexer/es-schema/

COPY assets/*.sh /usr/local/indexer/
RUN chmod +x /usr/local/indexer/*.sh
ENTRYPOINT ["/usr/local/indexer/start.sh"]