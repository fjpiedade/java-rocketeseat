FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y

COPY . .

RUN apt-get install maven -y
RUN mvn clean install

EXPOSE 9900

COPY --from=build /target/java-rocketeseat.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]