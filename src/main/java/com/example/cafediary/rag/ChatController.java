package com.example.cafediary.rag;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
	private final RagService ragService;

	public ChatController(RagService ragService) {
		this.ragService = ragService;
	}

	@PostMapping
	public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
		return ragService.chat(request.question());
	}
}
