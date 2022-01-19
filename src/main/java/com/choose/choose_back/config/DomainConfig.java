package com.choose.choose_back.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("com.choose.choose_back.domain")
@EnableJpaRepositories("com.choose.choose_back.repository")
@EnableTransactionManagement
public class DomainConfig {
}
