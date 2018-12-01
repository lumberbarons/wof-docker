FROM lumberbarons/wof-base:latest

# install spelunker deps
RUN apt-get update && apt-get install -y sudo gunicorn python-gevent gdal-bin && \ 
rm -rf /var/lib/apt/lists/* && pip install --no-cache-dir flask flask-cors pycountry

COPY assets/* /
RUN chmod +x /start.sh

COPY repos/whosonfirst-www-spelunker/ /usr/local/mapzen/whosonfirst-www-spelunker/

ENTRYPOINT ["/start.sh"]