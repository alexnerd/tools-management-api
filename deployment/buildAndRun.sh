#!/bin/sh
docker-compose down
docker-compose -p "tools-management" up -d --build
docker images | grep none | awk '{ print $3; }' | xargs docker rmi --force
rm -f tools-management.jar