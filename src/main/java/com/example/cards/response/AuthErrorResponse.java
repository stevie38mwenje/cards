package com.example.cards.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class AuthErrorResponse<T> {
	private String status_code;
	private String code;
	private String status;
	private String message;
	private String detail;
	private String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd hh:MM:ss a"));
	private T body;
}
