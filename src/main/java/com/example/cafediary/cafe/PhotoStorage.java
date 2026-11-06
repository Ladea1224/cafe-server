package com.example.cafediary.cafe;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoStorage {
	StoredPhoto upload(MultipartFile photo);
	void delete(String key);

	record StoredPhoto(String url, String key) { }
}
