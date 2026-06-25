package com.inetum.rlibrarybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(excludeName = {
        "com.okta.spring.boot.oauth.OktaOAuth2ResourceServerAutoConfig",
        "com.okta.spring.boot.oauth.OktaOAuth2AutoConfig"
})
public class RLibraryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RLibraryBackendApplication.class, args);
    }
}
