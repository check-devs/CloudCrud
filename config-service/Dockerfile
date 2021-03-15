FROM maven:3.6.3-openjdk-11

WORKDIR /config-server

COPY . .

RUN mvn package

EXPOSE 8888

CMD ["mvn", "spring-boot:run"]