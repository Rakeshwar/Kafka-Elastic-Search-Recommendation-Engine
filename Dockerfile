FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY search-service/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]