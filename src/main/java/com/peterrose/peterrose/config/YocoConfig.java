package com.peterrose.peterrose.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "yoco")
@Data
public class YocoConfig {
    

    private String secretKey;
    private String publicKey;
    private String apiUrl = "https://payments.yoco.com";
    private String webhookUrl;
    private String successUrl;
    private String cancelUrl;
}
