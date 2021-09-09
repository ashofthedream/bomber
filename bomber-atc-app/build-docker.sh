#!/usr/bin/env bash

gradle clean build

IMAGE_VERSION=$(gradle -q version)

docker build -t bomber/bomber-atc-app:${IMAGE_VERSION} .
docker build -t bomber/bomber-atc-app:latest .