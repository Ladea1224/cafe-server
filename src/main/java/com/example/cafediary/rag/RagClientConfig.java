package com.example.cafediary.rag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RagClientConfig {
	@Bean
	RestClient ragRestClient(
			@Value("${app.rag.base-url}") String baseUrl,
			@Value("${app.rag.connect-timeout}") Duration connectTimeout,
			@Value("${app.rag.read-timeout}") Duration readTimeout) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(connectTimeout);
		requestFactory.setReadTimeout(readTimeout);
		return RestClient.builder().baseUrl(baseUrl).requestFactory(requestFactory).build();
	}
}
