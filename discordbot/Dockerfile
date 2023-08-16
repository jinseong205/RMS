FROM openjdk:17-jdk-slim


ADD target/discordbot-0.0.1-SNAPSHOT.jar /app/discordbot-0.0.1-SNAPSHOT.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "discordbot-0.0.1-SNAPSHOT.jar"]
    