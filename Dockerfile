FROM adoptopenjdk/openjdk11:alpine-jre
COPY ./spring-websockets.jar /spring-websockets.jar
ENTRYPOINT ["java","-jar", "-Xss512k",  "/spring-websockets.jar"]