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
# RUN adduser --system --group www-data --uid 33
COPY --from=builder --chown=homed-web:homed-web /app/dependencies/ ./
COPY --from=builder --chown=homed-web:homed-web /app/spring-boot-loader/ ./
COPY --from=builder --chown=homed-web:homed-web /app/snapshot-dependencies/ ./
COPY --from=builder --chown=homed-web:homed-web /app/application/ ./
USER homed-web
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]