package com.example.shortener.application.port.in;

import com.example.shortener.domain.model.ShortUrl;

public interface CreateShortUrlUseCase {
    ShortUrl createShortUrl(String longUrl);
}
