FROM openjdk:17-jdk-alpine
MAINTAINER conductum.com
COPY target/c2-erp-0.0.1-SNAPSHOT.jar c2-erp-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/c2-erp-0.0.1-SNAPSHOT.jar"]