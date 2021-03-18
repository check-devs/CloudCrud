FROM maven:3.6.3-openjdk-11

WORKDIR /post-service

ENV PORT=8082

COPY . .

RUN mvn package

EXPOSE ${PORT}

CMD ["mvn", "spring-boot:run"]