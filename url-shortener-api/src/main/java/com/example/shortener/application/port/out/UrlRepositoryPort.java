package com.example.shortener.application.port.out;

import com.example.shortener.domain.model.ShortUrl;

import java.util.Optional;

public interface UrlRepositoryPort {
    ShortUrl save(ShortUrl shortUrl);

    Optional<ShortUrl> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    void incrementClickCount(String shortCode);
}
