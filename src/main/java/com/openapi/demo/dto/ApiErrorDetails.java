package com.openapi.demo.dto;

import java.time.ZonedDateTime;

public record ApiErrorDetails(String message, ZonedDateTime timestamp) {}
