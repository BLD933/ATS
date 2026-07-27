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

EXPOSE 8080

# Memory: 256 MB container limit — keep heap at 96m.
# CRITICAL: Do NOT add -XX:+AlwaysPreTouch. On Runsite's 0.1 vCPU containers
# pre-touching ~240 MB of pages adds 30-90s of startup delay, far exceeding
# the health-check grace period. UseSerialGC is fine for a low-heap app.
# TieredStopAtLevel=1 skips C2 compilation for fast startup.
# urandom avoids SecureRandom stalls on low-entropy VMs.
ENV JAVA_OPTS="-Xmx96m -Xss512k -XX:+UseSerialGC \
  -XX:MaxMetaspaceSize=96m -XX:ReservedCodeCacheSize=48m \
  -XX:+TieredCompilation -XX:TieredStopAtLevel=1 \
  -XX:+ExitOnOutOfMemoryError \
  -verbose:gc \
  -Djava.security.egd=file:/dev/./urandom \
  -Djava.net.preferIPv4Stack=true"

ENV GROQ_API_KEY=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]