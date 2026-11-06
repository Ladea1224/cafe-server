package com.example.cafediary.cafe;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CafeRequest(
		@NotBlank @Size(max = 120) String name,
		@NotBlank @Size(max = 500) String address,
		@NotNull @DecimalMin("0") @DecimalMax("5") @Digits(integer = 1, fraction = 1) BigDecimal rating,
		@NotNull @PastOrPresent LocalDate visitedAt,
		@Size(max = 5000) String memo) {
}
