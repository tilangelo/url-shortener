package com.example.shortener.api.controller;

import com.example.shortener.api.dto.ShortenRequest;
import com.example.shortener.api.dto.ShortenResponse;
import com.example.shortener.application.port.in.CreateShortUrlUseCase;
import com.example.shortener.domain.model.ShortUrl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ShortenController {

    private final CreateShortUrlUseCase createShortUrlUseCase;
    private final String baseUrl;

    public ShortenController(CreateShortUrlUseCase createShortUrlUseCase,
                             @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.createShortUrlUseCase = createShortUrlUseCase;
        this.baseUrl = baseUrl;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        ShortUrl shortUrl = createShortUrlUseCase.createShortUrl(request.getLongUrl());

        String fullUrl = baseUrl + "/" + shortUrl.getShortCode();

        return ResponseEntity.ok(new ShortenResponse(fullUrl));
    }

}
