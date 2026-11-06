package com.example.cafediary.cafe;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CafeService {
	private final CafeRepository repository;
	private final PhotoStorage photoStorage;

	public CafeService(CafeRepository repository, PhotoStorage photoStorage) {
		this.repository = repository;
		this.photoStorage = photoStorage;
	}

	public List<Cafe> findAll() { return repository.findAll(); }

	public Cafe findById(long id) {
		return repository.findById(id).orElseThrow(() -> new CafeNotFoundException(id));
	}

	public Cafe create(CafeRequest request, MultipartFile photo) {
		PhotoStorage.StoredPhoto uploaded = hasPhoto(photo) ? photoStorage.upload(photo) : null;
		long id;
		try {
			id = repository.insert(request, uploaded == null ? null : uploaded.url(), uploaded == null ? null : uploaded.key());
		} catch (RuntimeException e) {
			if (uploaded != null) photoStorage.delete(uploaded.key());
			throw e;
		}
		return findById(id);
	}

	public Cafe update(long id, CafeRequest request, MultipartFile photo) {
		Cafe current = findById(id);
		String oldKey = repository.findPhotoKeyById(id).orElse(null);
		PhotoStorage.StoredPhoto uploaded = hasPhoto(photo) ? photoStorage.upload(photo) : null;
		String photoUrl = uploaded == null ? current.photoUrl() : uploaded.url();
		String photoKey = uploaded == null ? oldKey : uploaded.key();
		try {
			if (!repository.update(id, request, photoUrl, photoKey)) throw new CafeNotFoundException(id);
		} catch (RuntimeException e) {
			if (uploaded != null) photoStorage.delete(uploaded.key());
			throw e;
		}
		if (uploaded != null && oldKey != null) photoStorage.delete(oldKey);
		return findById(id);
	}

	public void delete(long id) {
		findById(id);
		String key = repository.findPhotoKeyById(id).orElse(null);
		if (repository.delete(id)) photoStorage.delete(key);
	}

	private boolean hasPhoto(MultipartFile photo) { return photo != null && !photo.isEmpty(); }
}
