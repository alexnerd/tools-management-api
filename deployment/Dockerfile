FROM --platform=linux/arm64/v8 maven:3.9.4 AS build
LABEL maintainer="Aleksey Popov, alexnerd.com" description="Image for building tools-managment-api application"
COPY ./src /usr/src/app/src
COPY ./pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -Dmaven.test.skip
RUN jar xf /usr/src/app/target/tools-management.jar
RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 21  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*'  \
    /usr/src/app/target/tools-management.jar > deps.info
RUN jlink \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /tools-management.jar

FROM --platform=linux/arm64/v8 openjdk:21-oraclelinux8
LABEL maintainer="Aleksey Popov, alexnerd.com" description="Image with JDK 21 and tools managment application"
COPY --from=build /usr/src/app/target/tools-management.jar /usr/share/tools-management.jar

RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

ENTRYPOINT ["java", "-jar", "/usr/share/tools-management.jar"]