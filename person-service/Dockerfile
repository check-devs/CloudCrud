FROM maven:3.6.3-openjdk-11

WORKDIR /person-service

ENV PORT=8081

ENV SPANNER_EMULATOR_HOST="http://host.docker.internal:9010/"

COPY . .

EXPOSE 8081

CMD ["mvn", "package"]

CMD ["mvn", "spring-boot:run"]

