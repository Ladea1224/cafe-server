package com.example.cafediary.health;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {
	// CI/CD 배포 확인 시 이 값을 바꾸면 응답에서 새 배포 여부를 확인할 수 있습니다.
	private static final String DEPLOY_MARKER = "health-check-v1";

	private final JdbcTemplate jdbcTemplate;

	public HealthController(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@GetMapping
	public ResponseEntity<HealthResponse> health() {
		try {
			jdbcTemplate.queryForObject("SELECT 1", Integer.class);
			return ResponseEntity.ok(new HealthResponse("UP", "Database connection is available", DEPLOY_MARKER));
		} catch (DataAccessException exception) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
					.body(new HealthResponse("DOWN", "Database connection is unavailable", DEPLOY_MARKER));
		}
	}

	public record HealthResponse(String status, String message, String deployMarker) { }
}
