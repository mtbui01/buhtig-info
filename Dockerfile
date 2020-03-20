FROM gcr.io/distroless/java:11
ARG Github_Username
ARG Github_Token
ENV GITHUB_USERNAME=$Github_Username
ENV GITHUB_TOKEN=$Github_Token
COPY  ./target/*.jar app.jar
EXPOSE 10000
ENTRYPOINT ["java","-jar","/app.jar"]
