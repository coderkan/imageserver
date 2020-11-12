FROM openjdk:11
ADD ./target/imageserver-0.0.1-SNAPSHOT.jar /usr/src/imageserver-0.0.1-SNAPSHOT.jar
WORKDIR usr/src
ENTRYPOINT ["java","-jar", "imageserver-0.0.1-SNAPSHOT.jar"]