package com.example.cafediary.cafe;

public class CafeNotFoundException extends RuntimeException {
	public CafeNotFoundException(long id) { super("Cafe not found: " + id); }
}
