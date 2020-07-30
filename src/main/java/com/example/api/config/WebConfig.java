package com.example.api.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Configuration
public class WebConfig {

    @Value("${server.port}")
    private int port;

    @Bean
    public RestTemplate restTemplate() {

        HttpHost host = new HttpHost("localhost", port, "http");
        CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(credentialsProvider())
                .useSystemProperties()
                .build();

        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactoryDigestAuth(client, host);
        return new RestTemplate(requestFactory);
    }


    private CredentialsProvider credentialsProvider() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("user", "userPass"));
        return credentialsProvider;
    }

    public static class HttpComponentsClientHttpRequestFactoryDigestAuth
            extends HttpComponentsClientHttpRequestFactory {
        private final HttpHost host;

        public HttpComponentsClientHttpRequestFactoryDigestAuth(HttpClient httpClient, HttpHost host) {
            super(httpClient);
            this.host = host;
        }

        @Override
        protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
            return createHttpContext();
        }

        private HttpContext createHttpContext() {
            AuthCache authCache = new BasicAuthCache();
            DigestScheme digestAuth = new DigestScheme();
            digestAuth.overrideParamter("realm", "uzspring");
            digestAuth.overrideParamter("nonce", "");
            authCache.put(host, digestAuth);

            BasicHttpContext context = new BasicHttpContext();
            context.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
            return context;
        }
    }
}
