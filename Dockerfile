FROM docker.io/gradle:jdk26 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

COPY src ./src
RUN gradle bootJar -x test --no-daemon && \
    cp /app/build/libs/*.jar /app/app.jar

FROM docker.io/eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /app/app.jar .
COPY Caddyfile /etc/caddy/Caddyfile

RUN apt-get update -qq && apt-get install -y -qq debian-keyring debian-archive-keyring apt-transport-https curl > /dev/null 2>&1 && \
    curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg > /dev/null 2>&1 && \
    echo "deb [signed-by=/usr/share/keyrings/caddy-stable-archive-keyring.gpg] https://dl.cloudsmith.io/public/caddy/stable/deb/debian any-version main" > /etc/apt/sources.list.d/caddy-stable.list && \
    apt-get update -qq && apt-get install -y -qq caddy > /dev/null 2>&1 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

ENV SERVER_PORT=8081
ENV JAVA_OPTS="-Xmx100m -Xss256k -XX:+UseSerialGC -XX:MaxMetaspaceSize=64m"
ENV GROQ_API_KEY=""
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "caddy start --config /etc/caddy/Caddyfile && java $JAVA_OPTS -jar app.jar"]