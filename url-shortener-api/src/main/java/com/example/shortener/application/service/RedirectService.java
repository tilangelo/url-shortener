package com.example.shortener.application.service;

import com.example.shortener.application.port.in.RedirectUseCase;
import com.example.shortener.application.port.out.CachePort;
import com.example.shortener.application.port.out.UrlRepositoryPort;
import com.example.shortener.common.exception.NotFoundException;
import com.example.shortener.domain.model.ShortUrl;

import java.util.Optional;

public class RedirectService implements RedirectUseCase {

    private final CachePort cachePort;
    private final UrlRepositoryPort repository;

    public RedirectService(CachePort cachePort,
                           UrlRepositoryPort repository) {
        this.cachePort = cachePort;
        this.repository = repository;
    }

    @Override
    public String redirect(String shortCode) {
        //Кеш lookup
        Optional<String> cached = cachePort.get(shortCode);
        if (cached.isPresent()) {
            repository.incrementClickCount(shortCode);
            return cached.get();
        }


        //DB Fallback
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new NotFoundException("Ошибка поиска, не найден ШортКод: " + shortCode));

        //Кидаем обратно в кеш
        cachePort.save(shortCode, shortUrl.getLongUrl());

        repository.incrementClickCount(shortCode);
        return shortUrl.getLongUrl();
        //
    }
}
