FROM lumberbarons/wof-base:latest

# install deps
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# copy scripts and set entrypoint
COPY scripts/* /
RUN chmod +x /start.sh
ENTRYPOINT ["/start.sh"]