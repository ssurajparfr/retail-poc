# ---- Build Stage ----
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper + pom.xml
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd pom.xml ./
RUN chmod +x mvnw

# Cache dependencies
RUN ./mvnw -B -DskipTests dependency:go-offline

# Copy source
COPY src ./src

# Build application
RUN ./mvnw -B -DskipTests package

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the finalName jar
COPY --from=builder /app/target/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
