package com.choose.choose_back.config;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendgridConfiguration {
    @Value("${sendgrid.token}")
    private String token;
    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(token);
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("codes");
    }
}
