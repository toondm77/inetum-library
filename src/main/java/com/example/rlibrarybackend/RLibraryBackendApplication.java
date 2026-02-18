package com.example.rlibrarybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;

@SpringBootApplication
public class RLibraryBackendApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(RLibraryBackendApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openHomePage() throws IOException {
        // Launch default browser to the app root after startup
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler http://localhost:8080/swagger-ui/index.html#/");
    }
}
