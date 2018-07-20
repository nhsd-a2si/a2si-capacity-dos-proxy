FROM openjdk:8-jdk-alpine
VOLUME /tmp

ADD ./target/a2si-dos-proxy-0.0.1-SNAPSHOT.jar dos-proxy.jar
ADD ./keystore.jks keystore.jks

# Expose 7035, the default port used for Dos Proxy
EXPOSE 7035
ENV JAVA_OPTS=""
RUN ["apk", "update"]
RUN ["apk", "add", "tzdata"]
RUN ["ln", "-f", "-s", "/usr/share/zoneinfo/Europe/London", "/etc/localtime"]
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar dos-proxy.jar" ]