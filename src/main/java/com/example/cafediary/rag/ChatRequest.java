package com.example.cafediary.rag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
		@NotBlank(message = "question must not be blank")
		@Size(max = 4000, message = "question must be at most 4000 characters")
		String question) {
}
