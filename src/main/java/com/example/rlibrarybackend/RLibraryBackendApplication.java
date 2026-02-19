package com.example.rlibrarybackend;

import org.springframework.beans.factory.annotation.Value;
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
}
