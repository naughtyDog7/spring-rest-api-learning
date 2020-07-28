package com.example.api.config;

import org.apache.http.HttpHost;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.URI;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    }

    @Component
    public static class RestTemplateFactory implements FactoryBean<RestTemplate>, InitializingBean {

        private RestTemplate restTemplate;

        @Value("${server.port}")
        private int port;

        @Override
        public RestTemplate getObject() {
            return restTemplate;
        }

        @Override
        public Class<?> getObjectType() {
            return RestTemplate.class;
        }

        @Override
        public void afterPropertiesSet() {
            HttpHost host = new HttpHost("localhost", port, "http");
            ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(HttpClients.createDefault(), host);
            restTemplate = new RestTemplate(requestFactory);
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("user", "userPass"));
        }
    }

    public static class HttpComponentsClientHttpRequestFactoryBasicAuth
            extends HttpComponentsClientHttpRequestFactory {
        private final HttpHost host;

        public HttpComponentsClientHttpRequestFactoryBasicAuth(HttpClient httpClient, HttpHost host) {
            super(httpClient);
            this.host = host;
        }

        @Override
        protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
            return createHttpContext();
        }

        private HttpContext createHttpContext() {
            AuthCache authCache = new BasicAuthCache();
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(host, basicAuth);

            BasicHttpContext context = new BasicHttpContext();
            context.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
            return context;
        }
    }
}
