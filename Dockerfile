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

# Install curl for Docker HEALTHCHECK (also used by Runsite health probes)
RUN apt-get update && apt-get install -y curl --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

EXPOSE 8080

# Memory: 256 MB container limit — keep heap at 96m to leave ample room for
# metaspace, code cache, thread stacks, and JVM native overhead on Runsite's
# constrained 256 MB containers.
# Startup: tiered compilation stops at C1 for fast startup; urandom avoids
# SecureRandom stalls that can cause 30+ second delays on low-entropy VMs.
ENV JAVA_OPTS="-Xmx96m -Xss512k -XX:+UseSerialGC \
  -XX:MaxMetaspaceSize=96m -XX:ReservedCodeCacheSize=48m \
  -XX:+TieredCompilation -XX:TieredStopAtLevel=1 \
  -XX:+ExitOnOutOfMemoryError \
  -XX:+AlwaysPreTouch \
  -verbose:gc \
  -Djava.security.egd=file:/dev/./urandom \
  -Djava.net.preferIPv4Stack=true"

# Runsite uses a TCP health probe by default. This HEALTHCHECK provides an
# additional HTTP-level check via the /health endpoint defined in AtsController.
HEALTHCHECK --start-period=30s --interval=15s --timeout=5s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

ENV GROQ_API_KEY=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]