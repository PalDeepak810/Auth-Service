#Build stage

FROM maven:3.9.9-eclipse-temurin-21 As Builder

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests


#runtime stage

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]