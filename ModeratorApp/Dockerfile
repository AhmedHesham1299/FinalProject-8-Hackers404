FROM openjdk:25-ea-4-jdk-oraclelinux9

WORKDIR /app

COPY target/*.jar /target/miniapp.jar

EXPOSE 8060

ENTRYPOINT ["java", "-jar", "/target/miniapp.jar"]
