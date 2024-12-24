FROM adoptopenjdk/openjdk11:latest
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} self-web.jar
ENTRYPOINT ["java","-jar","/self-web.jar"]