#!/bin/bash

export activated=1

orig_dir=$(pwd)
dir=$(cd $(dirname -- $0) && pwd)
venv_deploy_dir=${dir}/_venv_deploy

if ! virtualenv --version; then
  echo "virtualenv is not installed, please install it on this host, before running $0"
  echo "  sudo pip install virtualenv"
  return 1
fi

mkdir -p ${venv_deploy_dir}
cd ${venv_deploy_dir}

virtualenv .

source bin/activate
pip install ansible==2.7.1.0
pip install boto3==1.9.45

cd ${orig_dir}
