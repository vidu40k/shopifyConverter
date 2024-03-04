FROM maven:3.8.4-openjdk-17 as build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean install

FROM openjdk:17-alpine

RUN addgroup -S app && adduser -S app -G app
USER app

USER root
RUN mkdir -p /tokens /products/motonational /products/revit /initialData/motonational && \
    chmod -R 777 /tokens /products /initialData

# Копирование файла credentials.json внутрь контейнера
COPY src/main/resources/credentials.json /credentials.json

WORKDIR /app
COPY --from=build /app/target/ShopifyConverter-0.0.1-SNAPSHOT.jar ./revit-app.jar

EXPOSE 8080:8080
ENTRYPOINT ["java", "-jar", "revit-app.jar"]