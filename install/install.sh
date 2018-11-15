#!/usr/bin/env bash

if [ -z $activated ]; then
    echo "Run: 'source activate.sh' before invoking this script"
    exit 1
fi

git submodule init
git submodule update

pushd playbooks

ansible-playbook --inventory=output/inventory install.yml

popd
