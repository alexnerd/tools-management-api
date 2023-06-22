FROM --platform=linux/arm64/v8 maven:3.8.5-openjdk-17 AS build
LABEL maintainer="Aleksey Popov, alexnerd.com" description="Image for building tools-managment-api application"
COPY ./src /usr/src/app/src
COPY ./pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -Dmaven.test.skip

FROM --platform=linux/arm64/v8 centos:latest
LABEL maintainer="Aleksey Popov, alexnerd.com" description="Image with JDK 17 and tools managment application"
RUN cd /etc/yum.repos.d/ \
  && sed -i 's/mirrorlist/#mirrorlist/g' /etc/yum.repos.d/CentOS-* \
  && sed -i 's|#baseurl=http://mirror.centos.org|baseurl=http://vault.centos.org|g' /etc/yum.repos.d/CentOS-* \
  && yum update -y \
  && yum -y install unzip \
  && yum -y install java-17-openjdk \
  && yum clean all
COPY --from=build /usr/src/app/target/tools-management.jar /usr/share/tools-management.jar
ENTRYPOINT ["java", "-jar", "/usr/share/tools-management.jar"]