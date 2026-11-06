package com.example.cafediary.cafe;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/cafes")
public class CafeController {
	private final CafeService service;

	public CafeController(CafeService service) { this.service = service; }

	@GetMapping
	public List<Cafe> findAll() { return service.findAll(); }

	@GetMapping("/{id}")
	public Cafe findById(@PathVariable long id) { return service.findById(id); }

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Cafe> create(@Valid @RequestPart("data") CafeRequest request,
			@RequestPart(value = "photo", required = false) MultipartFile photo) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, photo));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Cafe update(@PathVariable long id, @Valid @RequestPart("data") CafeRequest request,
			@RequestPart(value = "photo", required = false) MultipartFile photo) {
		return service.update(id, request, photo);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
