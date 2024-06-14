/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.config;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import com.namics.distrelec.occ.core.request.mapping.handler.CommerceHandlerMapping;

import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Spring configuration which replace <mvc:annotation-driven> tag. It allows override default RequestMappingHandlerMapping with our own
 * mapping handler
 */
@Configuration
@ImportResource({ "WEB-INF/config/v2/springmvc-v2-servlet.xml" })
public class WebConfig extends DelegatingWebMvcConfiguration {

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Resource(name = "messageConvertersV2")
    private List<HttpMessageConverter<?>> messageConvertersV2;

    @Resource
    private List<HandlerExceptionResolver> exceptionResolversV2;

    private ApplicationContext applicationContext;

    @Value("${distrelecB2BOCC.core.v2.config.webconfig.MultipartResolver.maxUploadSize}")
    private int maximumUploadSizeForMultipartResolver;

    @SuppressWarnings({ "deprecation", "squid:CallToDeprecatedMethod" })
    @Override
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping(final ContentNegotiationManager mvcContentNegotiationManager,
                                                                     final FormattingConversionService mvcConversionService,
                                                                     final ResourceUrlProvider mvcResourceUrlProvider) {
        final CommerceHandlerMapping handlerMapping = new CommerceHandlerMapping("v2");
        handlerMapping.setOrder(0);
        handlerMapping.setDetectHandlerMethodsInAncestorContexts(true);
        handlerMapping.setInterceptors(getInterceptors(mvcConversionService, mvcResourceUrlProvider));
        handlerMapping.setContentNegotiationManager(mvcContentNegotiationManager);
        /*
         * For more details about deprecation see: https://github.com/spring-projects/spring-framework/issues/24179
         */
        handlerMapping.setUseRegisteredSuffixPatternMatch(true);
        return handlerMapping;
    }

    @Override
    protected void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.addAll(messageConvertersV2);
    }

    @Override
    protected void configureHandlerExceptionResolvers(final List<HandlerExceptionResolver> exceptionResolvers) {
        final ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
        exceptionHandlerExceptionResolver.setApplicationContext(applicationContext);
        exceptionHandlerExceptionResolver.setContentNegotiationManager(mvcContentNegotiationManager());
        exceptionHandlerExceptionResolver.setMessageConverters(getMessageConverters());
        exceptionHandlerExceptionResolver.afterPropertiesSet();

        exceptionResolvers.add(exceptionHandlerExceptionResolver);
        exceptionResolvers.addAll(exceptionResolversV2);
        exceptionResolvers.add(new ResponseStatusExceptionResolver());
        exceptionResolvers.add(new DefaultHandlerExceptionResolver());
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException // NOSONAR
    {
        super.setApplicationContext(applicationContext);
        this.applicationContext = applicationContext;
    }

    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false).favorParameter(true);
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        final CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(maximumUploadSizeForMultipartResolver);
        return multipartResolver;
    }

}
