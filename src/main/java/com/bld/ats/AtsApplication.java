package com.bld.ats;

import com.bld.ats.config.ReverseProxyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class AtsApplication {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        ReverseProxyServer.start(port);

        System.setProperty("server.port", "8081");
        SpringApplication.run(AtsApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        ReverseProxyServer.markReady();
    }
}