FROM eclipse-temurin:21-jdk
WORKDIR /app
LABEL maintainer ="javaguides-net"
ADD   target/Calculator-0.0.1-SNAPSHOT.jar Calculator.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "Hello-World.jar"]
