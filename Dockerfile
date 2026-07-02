FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src src

RUN mvn clean package -DskipTests -B

RUN jar tf target/*.jar | grep -E "META-INF/MANIFEST.MF"

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN mkdir -p /app/logs && \
    addgroup -S appgroup && \
    adduser -S appuser -G appgroup && \
    chown -R appuser:appgroup /app/logs

COPY --from=build /app/target/*.jar app.jar

RUN jar tf app.jar | head -20

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]