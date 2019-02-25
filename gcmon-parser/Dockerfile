FROM adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.2.9-slim

COPY target/parser.jar /

CMD ["java", "-jar", "parser.jar", "-cluster"]
