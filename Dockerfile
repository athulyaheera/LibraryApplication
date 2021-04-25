FROM openjdk:11
MAINTAINER athulya
COPY target/library-app-1.0.0.jar library-app-1.0.0.jar
ENTRYPOINT ["java","-jar","/library-app-1.0.0.jar"]