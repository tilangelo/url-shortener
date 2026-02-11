package com.example.shortener.infrastructure.persistence.mapper;

import com.example.shortener.domain.model.ShortUrl;
import com.example.shortener.infrastructure.persistence.entity.UrlEntity;
import org.springframework.stereotype.Component;

@Component
public class UrlMapper {
    public UrlEntity toEntity(ShortUrl shortUrl) {
        return new UrlEntity(
                shortUrl.getId(),
                shortUrl.getShortCode(),
                shortUrl.getLongUrl(),
                shortUrl.getCreatedAt(),
                shortUrl.getExpiresAt()
        );
    }

    public ShortUrl toDomain(UrlEntity entity) {
        return new ShortUrl(
                entity.getId(),
                com.example.shortener.domain.valueobject.ShortCode.of(entity.getShortCode()),
                com.example.shortener.domain.valueobject.LongUrl.of(entity.getLongUrl()),
                entity.getCreatedAt(),
                entity.getExpiresAt()
        );
    }
}
