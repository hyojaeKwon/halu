FROM eclipse-temurin:17-jre
WORKDIR /app
COPY build/libs/newsletter-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java -jar /app/app.jar"]