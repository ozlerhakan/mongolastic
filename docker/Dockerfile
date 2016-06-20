FROM maven:3-jdk-8

RUN mkdir /mongolastic
ADD . /mongolastic

WORKDIR /mongolastic
RUN mvn compile
CMD mvn clean test
