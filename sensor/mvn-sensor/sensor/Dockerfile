##
## build 
##
FROM maven:3.8.3-jdk-11 as build
WORKDIR /home/app
COPY src ./src
COPY pom.xml .
# RUN mvn clean install -U
RUN mvn clean package

##
## Package stage
##
FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/sensor-1.0.jar /usr/local/lib/sensor.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/sensor.jar"]