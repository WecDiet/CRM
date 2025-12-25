FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY target/CRM-0.0.1-SNAPSHOT.war CRM-0.0.1-SNAPSHOT.war 

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "CRM-0.0.1-SNAPSHOT.war"]