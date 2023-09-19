FROM openjdk:17-jdk-slim


ADD target/demo-0.0.1-SNAPSHOT.jar /app/demo-0.0.1-SNAPSHOT.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "demo-0.0.1-SNAPSHOT.jar"]
