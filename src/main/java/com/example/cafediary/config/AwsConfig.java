package com.example.cafediary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {
	@Bean
	S3Client s3Client(@Value("${app.aws.region}") String region,
			@Value("${app.aws.access-key-id:}") String accessKeyId,
			@Value("${app.aws.secret-access-key:}") String secretAccessKey) {
		var builder = S3Client.builder().region(Region.of(region));
		if (accessKeyId.isBlank() != secretAccessKey.isBlank()) {
			throw new IllegalStateException("Configure both AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY, or neither.");
		}
		if (!accessKeyId.isBlank()) {
			builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)));
		} else {
			builder.credentialsProvider(DefaultCredentialsProvider.create());
		}
		return builder.build();
	}
}
