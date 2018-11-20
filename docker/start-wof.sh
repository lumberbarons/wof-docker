#!/bin/sh

while test $# != 0; do
    case "$1" in
        -k) NEXTZEN_KEY=${2} ;;
        -g) WOFDATA_GITHUB_URL=${2} ;;
    esac
    shift
done

if [ -z ${NEXTZEN_KEY} ] || [ -z ${WOFDATA_GITHUB_URL} ]; then
    echo "Usage: $0 -k [nextzen api key] -g [wof data github url]"
    exit 1
fi

#docker-compose up -d