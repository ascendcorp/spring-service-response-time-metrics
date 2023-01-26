package com.example.demo.configuration;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


@Configuration
public class RestTemplateConfiguration {

    private static final String TLS_VERSION = "TLSv1";

    /*
    private void setClientRequestInterceptorToInterceptor(RestTemplate restTemplate) {
        List interceptors = restTemplate.getInterceptors();
        if (interceptors == null) {
            // restTemplate.setInterceptors(Collections.singletonList(new ClientRequestInterceptor()));
        } else { interceptors.add(new ServiceRequestInterceptor(null));
            restTemplate.setInterceptors(interceptors);
        }
    }
    */

    @Bean
    @Primary
    RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpComponentsClientHttpRequestFactory httpComponentFactory = new HttpComponentsClientHttpRequestFactory();
        httpComponentFactory.setHttpClient(this.httpClient());
        RestTemplate restTemplate = new RestTemplate(httpComponentFactory);

        // change to add from library
        // setClientRequestInterceptorToInterceptor(restTemplate);

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return false;
            }
        });

        return restTemplate;
    }

    @Bean
    public CloseableHttpClient httpClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContextBuilder builder = SSLContexts.custom();
        builder.setProtocol(TLS_VERSION);

        builder.loadTrustMaterial(null, (chain, authType) -> true);

        SSLContext sslContext = builder.build();

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslsf)
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        return  HttpClientBuilder.create().setSSLSocketFactory(sslsf).setConnectionManager(connectionManager).build();
    }
}