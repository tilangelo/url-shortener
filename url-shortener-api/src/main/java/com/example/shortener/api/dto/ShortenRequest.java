package com.example.shortener.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ShortenRequest {
    @NotBlank(message = "URL is required")
    @Size(max = 2048, message = "URL is too long")
    private String longUrl;
}
