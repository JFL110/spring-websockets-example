FROM adoptopenjdk/openjdk11:alpine-jre
COPY ./spring-websockets.jar /spring-websockets.jar
ENTRYPOINT ["java","-jar","/spring-websockets.jar"]