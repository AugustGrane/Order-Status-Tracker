FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn dependency:go-offline
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV SPRING_DEVTOOLS_REMOTE_SECRET=secret

EXPOSE 8080

CMD ["java", "-jar", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'", "app.jar"]
