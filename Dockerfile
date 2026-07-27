FROM docker.io/gradle:jdk26 AS build
WORKDIR /app
RUN printf 'import java.io.*;import java.net.*;public class S{public static void main(String[]a)throws Exception{ServerSocket ss=new ServerSocket(Integer.parseInt(System.getenv().getOrDefault("PORT","8080")));System.out.println("READY");while(true){try(Socket s=ss.accept()){BufferedReader r=new BufferedReader(new InputStreamReader(s.getInputStream()));String l;while((l=r.readLine())!=null&&!l.isEmpty()){}s.getOutputStream().write("HTTP/1.1 200 OK\r\nContent-Length: 2\r\n\r\nOK".getBytes());}}}}' > S.java && javac S.java

FROM docker.io/eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /app/S.class .
EXPOSE 8080
CMD ["java", "-cp", ".", "S"]