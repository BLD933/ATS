FROM docker.io/gradle:jdk26 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

COPY src ./src
RUN gradle bootJar -x test --no-daemon && \
    cp /app/build/libs/*.jar /app/app.jar

FROM docker.io/library/caddy:2-alpine AS caddy

FROM docker.io/eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /app/app.jar .
COPY --from=caddy /usr/bin/caddy /usr/bin/caddy
COPY Caddyfile /etc/caddy/Caddyfile
RUN mkdir -p /etc/caddy

ENV SERVER_PORT=8081
ENV JAVA_OPTS="-Xmx100m -Xss256k -XX:+UseSerialGC -XX:MaxMetaspaceSize=64m"
ENV GROQ_API_KEY=""
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "caddy start --config /etc/caddy/Caddyfile && java $JAVA_OPTS -jar app.jar"]