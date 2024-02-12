FROM maven:3.8.4-openjdk-17 as build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean install

FROM openjdk:17-alpine

RUN addgroup -S app && adduser -S app -G app
USER app

WORKDIR /app
COPY --from=build /app/target/ShopifyConverter-0.0.1-SNAPSHOT.jar ./revit-app.jar

ENTRYPOINT ["java", "-jar", "revit-app.jar"]
