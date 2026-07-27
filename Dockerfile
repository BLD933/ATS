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

ENV JAVA_OPTS="-Xmx128m -Xss256k -XX:+UseSerialGC \
  -XX:MaxMetaspaceSize=64m -XX:ReservedCodeCacheSize=32m \
  -XX:+TieredCompilation -XX:TieredStopAtLevel=1 \
  -XX:+ExitOnOutOfMemoryError \
  -Djava.security.egd=file:/dev/urandom"

ENV GROQ_API_KEY=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]