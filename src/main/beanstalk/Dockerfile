FROM williamyeh/docker-java8

ADD aroma-authentication-service-1.3-SNAPSHOT.jar aroma-authentication-service.jar
ADD lib lib

RUN apt-get update -y
RUN apt-get install wget curl -y

EXPOSE 7026

CMD java -jar aroma-authentication-service.jar --port 7026
