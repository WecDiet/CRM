FROM maven:3.9.6-eclipse-temurin-17 AS build
RUN apt-get update && apt-get install -y maven && apt-get clean

WORKDIR /app

COPY target/CRM-0.0.1-SNAPSHOT.war CRM-0.0.1-SNAPSHOT.war 

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "CRM-0.0.1-SNAPSHOT.war"]