FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY target/SimpleConsumer-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
