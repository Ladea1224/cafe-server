package com.example.cafediary.rag;

import java.util.List;

public record ChatResponse(String answer, List<RagSource> sources) {
	public ChatResponse {
		sources = sources == null ? List.of() : List.copyOf(sources);
	}
}
