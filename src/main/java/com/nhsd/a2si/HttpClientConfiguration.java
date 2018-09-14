package com.nhsd.a2si;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/*
 * This class constructs 2 beans, "dosRequestConfig" and "dosHttpClient" with settings for 
 * timeouts and connections that come from application settings in application.yml etc.
 * These beans are later effectively renamed to "requestConfig" and "httpClient" in the class BeanRedefinitions 
 * after the existing "requestConfig" and "httpClient", that come from Capacity-Service-Client, are removed.
 * Zuul uses a bean called "httpClient" for the connections it creates - which is why we need to control
 * the connection properties.
 * Zuul is used to connect to DoS. DoS can receive many concurrent requests, but each request might have a 1 - 10 second
 * typical response time.
 */

@Configuration
public class HttpClientConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientConfiguration.class);		

	private int socketTimeout;   // max milliseconds to wait for data
	private int connectTimeout;  // max milliseconds to wait for connection
	private int connectionRequestTimeout; // max milliseconds to wait when requesting a connection from the connection manager
	private int maxConnTotal;    // max concurrent connections
	private int maxConnPerRoute; // max concurrent connections to one url


	@Bean
    public RequestConfig dosRequestConfig(
        	@Value("${dos.httpClient.socketTimeout}") int socketTimeout,
        	@Value("${dos.httpClient.connectTimeout}") int connectTimeout,
        	@Value("${dos.httpClient.connectionRequestTimeout}") int connectionRequestTimeout
) {
    	this.socketTimeout = socketTimeout;
    	this.connectTimeout = connectTimeout;
    	this.connectionRequestTimeout = connectionRequestTimeout;
		
        return RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();
    }
    
    public HttpClientBuilder httpClientBuilder(CredentialsProvider credentialsProvider) {
    	HttpClientBuilder builder = HttpClientBuilder.create();
    	builder.setDefaultRequestConfig(dosRequestConfig(socketTimeout, connectTimeout, connectionRequestTimeout));
    	builder.setMaxConnTotal(maxConnTotal);
    	builder.setMaxConnPerRoute(maxConnPerRoute);
    	builder.setDefaultCredentialsProvider(credentialsProvider);
    	return builder;
    }

	public CredentialsProvider credentialsProvider(String capacityServiceUsername, String capacityServicePassword){
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(capacityServiceUsername, capacityServicePassword);
		provider.setCredentials(AuthScope.ANY, credentials);
		return provider;
	}
    
    @Primary @Bean
	public CloseableHttpClient dosHttpClient(
        	@Value("${dos.httpClient.maxConnTotal}") int maxConnTotal,
        	@Value("${dos.httpClient.maxConnPerRoute}") int maxConnPerRoute,
        	@Value("${dos.httpClient.socketTimeout}") int socketTimeout,
        	@Value("${dos.httpClient.connectTimeout}") int connectTimeout,
        	@Value("${dos.httpClient.connectionRequestTimeout}") int connectionRequestTimeout,
			@Value("${capacity.service.username}") String capacityServiceUsername,
			@Value("${capacity.service.password}") String capacityServicePassword
    		) {
    	this.maxConnTotal = maxConnTotal;
    	this.maxConnPerRoute = maxConnPerRoute;
    	this.socketTimeout = socketTimeout;
    	this.connectTimeout = connectTimeout;
    	this.connectionRequestTimeout = connectionRequestTimeout;
    	
    	LOGGER.info("DoS maxConnTotal=" + maxConnTotal);
    	LOGGER.info("DoS maxConnPerRoute=" + maxConnPerRoute);
    	LOGGER.info("DoS socketTimeout=" + socketTimeout);
    	LOGGER.info("DoS connectTimeout=" + connectTimeout);
    	LOGGER.info("DoS connectionRequestTimeout=" + connectionRequestTimeout);
    	
    	return httpClientBuilder(credentialsProvider(capacityServiceUsername, capacityServicePassword)).build();
    }

}
