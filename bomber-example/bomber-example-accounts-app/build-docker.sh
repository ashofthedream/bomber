#!/usr/bin/env bash

#gradle clean build

IMAGE_VERSION=$(gradle -q version)

docker build -t bomber/bomber-example-accounts-app:${IMAGE_VERSION} .
docker build -t bomber/bomber-example-accounts-app:latest .