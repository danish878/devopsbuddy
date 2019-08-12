package com.devopsbuddy.config;

import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.backend.service.SmptEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("prod")
@PropertySource("file:///${user.home}/desktop/devopsbuddy_properties/application-prod.properties")
public class ProductionConfig {

    @Value("${stripe.prod.secret.key}")
    private String stripeProdKey;

    @Bean
    public EmailService emailService() {
        return new SmptEmailService();
    }

    @Bean
    public String stripeKey() {
        return stripeProdKey;
    }
}
