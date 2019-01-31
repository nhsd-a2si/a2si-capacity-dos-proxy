FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG VERSION
ARG PROFILE

ADD ./target/a2si-dos-proxy-${VERSION}.jar dos-proxy.jar
# ADD ./target/a2si-dos-proxy-1.2.3.jar dos-proxy.jar
ADD ./keystore.jks keystore.jks

# Expose 7030, the default port used for Dos Proxy
EXPOSE 7030
ENV JAVA_OPTS=""
RUN ["apk", "update"]
RUN ["apk", "add", "tzdata"]
RUN ["ln", "-f", "-s", "/usr/share/zoneinfo/Europe/London", "/etc/localtime"]
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar -Dspring.profiles.active=$PROFILE dos-proxy.jar" ]
