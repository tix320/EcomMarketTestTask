FROM openjdk:17

COPY build/libs/EcomMarketTestTask-1.0.jar /app/app.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "app.jar"]
