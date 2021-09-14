#!/usr/bin/env bash

IMAGE_VERSION=$(gradle -q version)

docker build -t bomber/bomber-example-carrier-app:${IMAGE_VERSION} .
docker build -t bomber/bomber-example-carrier-app:latest .