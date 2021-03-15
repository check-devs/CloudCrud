FROM maven:3.6.3-openjdk-11

WORKDIR /zuul-server

COPY . .

RUN mvn package

EXPOSE 8080

CMD ["mvn", "spring-boot:run"]