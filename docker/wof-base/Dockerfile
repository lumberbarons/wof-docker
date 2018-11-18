FROM ubuntu:16.04

# install base packages
RUN apt-get update && \
apt-get install -y git python-pip python-pyparsing python-setuptools libpython-dev libssl-dev libffi-dev && \ 
rm -rf /var/lib/apt/lists/*

# install python packages
RUN python -m pip install --upgrade --force pip && \
pip install --no-cache-dir --upgrade setuptools && \
pip install --no-cache-dir --upgrade pyopenssl ndg-httpsclient pyasn1 'requests[security]'

# install wof python tools
WORKDIR /usr/local/mapzen
RUN git clone https://github.com/whosonfirst/py-mapzen-whosonfirst.git && \
cd py-mapzen-whosonfirst && pip install --no-cache-dir --upgrade -r requirements.txt --process-dependency-links . && \
cd .. && rm -rf py-mapzen-whosonfirst