package com.peterrose.peterrose.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Yoco Payment Gateway Configuration
 */
@Configuration
@ConfigurationProperties(prefix = "yoco")
@Data
public class YocoConfig {
    
    /**
     * Yoco Secret Key (from Yoco Dashboard)
     */
    private String secretKey;
    
    /**
     * Yoco Public Key (for frontend)
     */
    private String publicKey;
    
    /**
     * Yoco API Base URL
     */
    private String apiUrl = "https://payments.yoco.com";
    
    /**
     * Payment webhook URL (your server endpoint)
     */
    private String webhookUrl;
    
    /**
     * Success redirect URL (after successful payment)
     */
    private String successUrl;
    
    /**
     * Cancel redirect URL (if payment cancelled)
     */
    private String cancelUrl;
}
