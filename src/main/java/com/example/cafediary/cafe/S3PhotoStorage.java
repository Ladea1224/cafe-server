package com.example.cafediary.cafe;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class S3PhotoStorage implements PhotoStorage {
	private static final Map<String, String> EXTENSIONS = Map.of(
			"image/jpeg", "jpg", "image/png", "png", "image/gif", "gif", "image/webp", "webp");
	private final S3Client s3Client;
	private final String region;
	private final String bucket;
	private final String publicBaseUrl;

	public S3PhotoStorage(S3Client s3Client,
			@Value("${app.aws.region}") String region,
			@Value("${app.aws.s3.bucket}") String bucket,
			@Value("${app.aws.s3.public-base-url}") String publicBaseUrl) {
		this.s3Client = s3Client;
		this.region = region;
		this.bucket = bucket;
		this.publicBaseUrl = publicBaseUrl;
	}

	@Override
	public StoredPhoto upload(MultipartFile photo) {
		if (bucket.isBlank()) throw new IllegalStateException("S3_BUCKET must be configured to upload photos.");
		String contentType = photo.getContentType() == null ? "" : photo.getContentType().toLowerCase(Locale.ROOT);
		String extension = EXTENSIONS.get(contentType);
		if (extension == null) throw new InvalidPhotoException("Only JPEG, PNG, GIF, and WebP images are supported.");
		String key = "cafes/" + UUID.randomUUID() + "." + extension;
		try {
			s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType).build(),
					RequestBody.fromInputStream(photo.getInputStream(), photo.getSize()));
		} catch (IOException e) {
			throw new IllegalStateException("Could not read the uploaded photo.", e);
		}
		String base = publicBaseUrl.isBlank()
				? "https://" + bucket + ".s3." + region + ".amazonaws.com"
				: publicBaseUrl.replaceAll("/+$", "");
		return new StoredPhoto(base + "/" + key, key);
	}

	@Override
	public void delete(String key) {
		if (key != null && !key.isBlank()) {
			s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
		}
	}
}
