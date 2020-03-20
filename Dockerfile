FROM gcr.io/distroless/java:11
ARG App_Username=donaldduck
ARG App_Token=donaldduck
ENV GITHUB_USERNAME=${App_Username}
ENV GITHUB_TOKEN=${App_Token}
COPY  ./target/*.jar app.jar
EXPOSE 10000
ENTRYPOINT ["java","-jar","/app.jar"]
