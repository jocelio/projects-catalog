FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/limitless-dawn-24766-0.0.1-SNAPSHOT-standalone.jar /limitless-dawn-24766/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/limitless-dawn-24766/app.jar"]
