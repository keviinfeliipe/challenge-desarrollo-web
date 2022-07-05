FROM openjdk:16-jdk-alpine
ARG JAR_FILE=target/cargame-1.0-SNAPSHOT.jar

WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]