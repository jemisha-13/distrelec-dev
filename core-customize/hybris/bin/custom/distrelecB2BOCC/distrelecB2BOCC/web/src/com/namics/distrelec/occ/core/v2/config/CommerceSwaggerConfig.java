package com.namics.distrelec.occ.core.v2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import de.hybris.platform.swagger.ApiDocInfo;

@Configuration
@PropertySource("classpath:/v2/swagger.properties")
@ImportResource(value = "classpath*:/swagger/swaggerintegration/web/spring/*-web-spring.xml")
public class CommerceSwaggerConfig {
    @Bean("apiDocInfo")
    public ApiDocInfo apiDocInfo() {
        return () -> "distrelecB2BOCC.v2";
    }
}
