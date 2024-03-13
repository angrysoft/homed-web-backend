FROM eclipse-temurin:21 AS base

FROM base AS builder
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN ./mvnw clean install -DskipTests
RUN java -Djarmode=layertools -jar target/homed-web-backend-0.1.0.jar extract

FROM base AS runner
WORKDIR /app
RUN adduser --system --group http
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
USER http
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]