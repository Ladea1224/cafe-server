package com.example.cafediary.rag;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.TimeoutException;

@Service
public class RagService {
	private final RestClient ragRestClient;

	public RagService(RestClient ragRestClient) {
		this.ragRestClient = ragRestClient;
	}

	public ChatResponse chat(String question) {
		try {
			ChatResponse response = ragRestClient.post()
					.uri("/chat")
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.body(new ChatRequest(question))
					.retrieve()
					.onStatus(HttpStatusCode::isError, (request, upstreamResponse) -> {
						throw new RagServiceException(HttpStatus.BAD_GATEWAY,
								"RAG server returned HTTP " + upstreamResponse.getStatusCode().value());
					})
					.body(ChatResponse.class);

			if (response == null) {
				throw new RagServiceException(HttpStatus.BAD_GATEWAY, "RAG server returned an empty response.");
			}
			return response;
		} catch (RagServiceException exception) {
			throw exception;
		} catch (ResourceAccessException exception) {
			if (isTimeout(exception)) {
				throw new RagServiceException(HttpStatus.GATEWAY_TIMEOUT, "RAG server request timed out.");
			}
			throw new RagServiceException(HttpStatus.SERVICE_UNAVAILABLE, "Could not connect to the RAG server.");
		} catch (RestClientException exception) {
			throw new RagServiceException(HttpStatus.BAD_GATEWAY, "RAG server returned an invalid response.");
		}
	}

	private boolean isTimeout(Throwable exception) {
		for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
			if (cause instanceof SocketTimeoutException
					|| cause instanceof HttpTimeoutException
					|| cause instanceof TimeoutException) {
				return true;
			}
		}
		return false;
	}
}
