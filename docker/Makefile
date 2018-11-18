build: build-base build-spelunker build-nginx

build-base:
	docker build -t lumberbarons/wof-base:latest wof-base/

build-spelunker:
	docker build -t lumberbarons/wof-spelunker:latest wof-spelunker/

build-nginx:
	docker build -t lumberbarons/wof-nginx:latest wof-nginx/

build-indexer:
	docker build -t lumberbarons/wof-indexer:latest wof-indexer/