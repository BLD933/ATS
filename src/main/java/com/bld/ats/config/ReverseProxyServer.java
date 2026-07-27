package com.bld.ats.config;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

public class ReverseProxyServer {

    private static final int INTERNAL_PORT = 8081;
    private static volatile boolean springReady = false;

    public static void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/health", exchange -> {
            byte[] response = "{\"status\":\"UP\"}".getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        server.createContext("/", exchange -> {
            if (!springReady) {
                byte[] response = "Service Starting".getBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(503, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
                return;
            }
            proxy(exchange);
        });

        server.start();
        System.out.println("[proxy] listening on :" + port);
    }

    private static void proxy(HttpExchange exchange) throws IOException {
        try {
            URL url = new URL("http://localhost:" + INTERNAL_PORT + exchange.getRequestURI());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(exchange.getRequestMethod());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(60000);

            exchange.getRequestHeaders().forEach((key, values) -> {
                if (key != null && !key.equalsIgnoreCase("Host")) {
                    for (String value : values) {
                        conn.addRequestProperty(key, value);
                    }
                }
            });

            try (OutputStream os = conn.getOutputStream()) {
                exchange.getRequestBody().transferTo(os);
            }

            int status = conn.getResponseCode();
            InputStream is = (status >= 400) ? conn.getErrorStream() : conn.getInputStream();
            long contentLength = conn.getContentLengthLong();

            conn.getHeaderFields().forEach((key, values) -> {
                if (key != null && !key.equalsIgnoreCase("Transfer-Encoding")) {
                    for (String value : values) {
                        exchange.getResponseHeaders().add(key, value);
                    }
                }
            });

            exchange.sendResponseHeaders(status, contentLength);
            if (is != null) {
                try (OutputStream os = exchange.getResponseBody()) {
                    is.transferTo(os);
                }
            }
            exchange.close();
        } catch (Exception e) {
            System.err.println("[proxy] backend unavailable: " + e.getMessage());
            byte[] msg = "Backend Starting".getBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(503, msg.length);
            exchange.getResponseBody().write(msg);
            exchange.close();
        }
    }

    public static void markReady() {
        springReady = true;
        System.out.println("[proxy] Spring Boot ready — proxying traffic");
    }
}
