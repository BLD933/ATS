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
# Memory: 256 MB container limit — keep heap at 128m to leave room for
# metaspace, code cache, and JVM overhead.
# Startup: tiered compilation stops at C1 for fast startup; urandom avoids
# SecureRandom stalls that can cause 30+ second delays on low-entropy VMs.
ENV JAVA_OPTS="-Xmx128m -Xss512k -XX:+UseSerialGC \
  -XX:MaxMetaspaceSize=96m -XX:ReservedCodeCacheSize=48m \
  -XX:+TieredCompilation -XX:TieredStopAtLevel=1 \
  -XX:+ExitOnOutOfMemoryError \
  -Djava.security.egd=file:/dev/./urandom"
ENV GROQ_API_KEY=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]