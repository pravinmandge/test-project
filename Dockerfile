FROM maven:onbuild AS buildenv

FROM openjdk:jre-alpine
COPY --from=buildenv /usr/src/app/target/test-project-0.0.1-SNAPSHOT.jar /test-project.jar
EXPOSE 80
CMD ["java", "-jar", "/test-project.jar"]