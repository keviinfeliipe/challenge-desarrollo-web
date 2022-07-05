FROM openjdk:16-jdk-alpine
ARG JAR_FILE=target/cargame-1.0-SNAPSHOT.jar

WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
EXPOSE $PORT
ENTRYPOINT ["java","-jar","app.jar"]