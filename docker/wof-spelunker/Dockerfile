FROM lumberbarons/wof-base:latest

# install spelunker deps
RUN apt-get update && apt-get install -y sudo gunicorn python-gevent gdal-bin && \ 
rm -rf /var/lib/apt/lists/* && pip install --no-cache-dir flask flask-cors pycountry

RUN cd /usr/local/mapzen && git clone https://github.com/lumberbarons/whosonfirst-www-spelunker.git

# copy scripts and set entrypoint
COPY scripts/* /
RUN chmod +x /start.sh
ENTRYPOINT ["/start.sh"]