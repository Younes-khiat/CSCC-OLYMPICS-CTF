FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app
COPY . .

# give execution rights to the gradle wrapper
RUN chmod +x gradlew

# build the application
RUN ./gradlew bootJar --no-daemon

# production stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# create a non-root user
RUN useradd -m ctf
USER ctf

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
