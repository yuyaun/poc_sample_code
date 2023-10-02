# Build stage
FROM maven:3.8.4-jdk-11-slim AS build
COPY . /app
RUN mvn -f /app/pom.xml clean package -DskipTests

# Package stage
FROM openjdk:11-jre-slim
COPY --from=build /app/spring-boot-mvc-demo/target/spring-boot-mvc-demo-0.0.1-SNAPSHOT.jar /usr/local/lib/spring-boot-mvc-demo.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/spring-boot-mvc-demo.jar"]
# ENTRYPOINT [ "/bin/sh" ]
