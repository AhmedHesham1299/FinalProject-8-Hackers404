FROM openjdk:25-ea-4-jdk-oraclelinux9
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"] 