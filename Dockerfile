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

# socat keeps port 8080 open while JVM starts (slow on 0.1 vCPU)
RUN apt-get update -qq && apt-get install -y -qq socat > /dev/null 2>&1 && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

# Minimal JVM for 256MB / 0.1 vCPU
ENV JAVA_OPTS="-Xmx64m -Xss256k -XX:+UseSerialGC \
  -XX:MaxMetaspaceSize=64m -XX:ReservedCodeCacheSize=32m \
  -XX:+TieredCompilation -XX:TieredStopAtLevel=1 \
  -XX:+ExitOnOutOfMemoryError \
  -Djava.security.egd=file:/dev/urandom"

ENV GROQ_API_KEY=""

# Start socat on 8080 → Java on 9090; then start Java
ENTRYPOINT ["sh", "-c", "socat TCP-LISTEN:8080,fork,reuseaddr TCP:127.0.0.1:9090 & java $JAVA_OPTS -jar app.jar --server.port=9090"]