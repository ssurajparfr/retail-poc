# ---- Build Stage ----
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd ./

# Copy project files
COPY pom.xml .
COPY src ./src

# Make wrapper executable
RUN chmod +x mvnw

# Copy only pom.xml first
COPY pom.xml .
RUN ./mvnw -B -DskipTests dependency:go-offline

# Then copy source code
COPY src ./src
RUN ./mvnw -B -DskipTests package

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the stable-name jar from the builder stage
COPY --from=builder /app/target/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
