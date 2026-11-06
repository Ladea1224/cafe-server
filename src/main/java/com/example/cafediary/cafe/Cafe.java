package com.example.cafediary.cafe;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Cafe(
		Long id,
		String name,
		String address,
		BigDecimal rating,
		LocalDate visitedAt,
		String memo,
		String photoUrl) {
}
