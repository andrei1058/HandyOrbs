FROM maven:adoptopenjdk

WORKDIR /home/minecraft
RUN mvn clean install