# https://medium.com/@mprokopov/deployment-of-clojure-app-to-production-with-docker-9dbffeac6ef5
FROM java:8-alpine
RUN mkdir -p /app /app/resources
WORKDIR /app
COPY target/chatr-server.jar .
ADD resources /app/resources
COPY scripts/run-server-jar scripts/run-server-jar
ENTRYPOINT ["scripts/run-server-jar"]
CMD ["chatr-server.jar"]
EXPOSE 8080
