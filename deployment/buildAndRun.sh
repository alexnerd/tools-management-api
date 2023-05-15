#!/bin/sh
docker-compose down
cd ..
./mvnw clean package -Dmaven.test.skip
cp ./target/tools-management.jar ./deployment
cd ./deployment || exit
docker-compose -p "tools-management" up -d --build
docker images | grep none | awk '{ print $3; }' | xargs docker rmi --force
rm -f tools-management.jar