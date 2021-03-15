FROM maven:3.6.3-openjdk-11

WORKDIR /eureka-server

COPY . .

RUN mvn package

EXPOSE 8761

CMD ["mvn", "spring-boot:run"]