build-all: build-indexer build-spelunker build-proxy

build-base:
	docker build -t lumberbarons/wof-base:latest wof-base/

build-indexer:
	docker build -t lumberbarons/wof-indexer:latest wof-indexer/

build-spelunker:
	cd wof-spelunker && ./build.sh

build-proxy:
	docker build -t lumberbarons/wof-proxy:latest wof-proxy/