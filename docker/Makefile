build-all: build-indexer build-data build-spelunker build-proxy

build-base:
	docker build -t lumberbarons/wof-base:latest wof-base/

build-indexer:
	docker build -t lumberbarons/wof-indexer:latest wof-indexer/

build-data:
	docker build -t lumberbarons/wof-data:latest wof-data/

build-spelunker:
	docker build -t lumberbarons/wof-spelunker:latest wof-spelunker/

build-proxy:
	docker build -t lumberbarons/wof-proxy:latest wof-proxy/