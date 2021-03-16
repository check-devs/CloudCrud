FROM maven:3.6.3-openjdk-11

WORKDIR /bigquery-service
COPY . .

RUN mvn package

EXPOSE 8083

CMD ["mvn", "spring-boot:run"]