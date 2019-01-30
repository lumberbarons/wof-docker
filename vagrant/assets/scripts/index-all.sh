#!/usr/bin/env bash

DATA_DIR=${1}
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

for filename in ${DATA_DIR}/*.bz2; do
    ${SCRIPT_DIR}/index-bundle.sh $filename
done