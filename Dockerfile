FROM openjdk:16-jdk

EXPOSE 8080
RUN mkdir /app
COPY ./build/install/ProjConnectAPI/ /app/
WORKDIR /app/bin
CMD ["./ProjConnectAPI"]
