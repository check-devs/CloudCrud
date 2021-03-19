FROM maven:3.6.3-openjdk-11

WORKDIR /bigquery-service

ENV GOOGLE_APPLICATION_CREDENTIALS=src/main/resources/cloud-crud-304511-1613e3453d69.json

COPY . .

RUN mvn package

EXPOSE 8083

CMD ["mvn", "spring-boot:run"]