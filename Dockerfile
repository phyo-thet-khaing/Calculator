FROM eclipse-temurin:21-jdk
WORKDIR /app
LABEL maintainer ="javaguides-net"
ADD   target/Hello-World-0.0.1-SNAPSHOT.jar Hello-World.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "Hello-World.jar"]