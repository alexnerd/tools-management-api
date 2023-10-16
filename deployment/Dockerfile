FROM --platform=linux/arm64/v8 maven:3.9.4 AS build
LABEL maintainer="Aleksey Popov, alexnerd.com" description="Image for building tools-managment-api application"
COPY ./src /usr/src/app/src
COPY ./pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -Dmaven.test.skip

FROM --platform=linux/arm64/v8 openjdk:21-oraclelinux8
LABEL maintainer="Aleksey Popov, alexnerd.com" description="Image with JDK 21 and tools managment application"
COPY --from=build /usr/src/app/target/tools-management.jar /usr/share/tools-management.jar
ENTRYPOINT ["java", "-jar", "/usr/share/tools-management.jar"]