FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine as alp1

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8001

ENTRYPOINT ["java",  "-jar", "/app.jar"]
