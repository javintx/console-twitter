# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY ./build/libs/console-twitter-1.0.0.jar .
CMD ["java", "-jar", "console-twitter-1.0.0.jar"]
