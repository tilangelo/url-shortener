package com.example.shortener.application.service;

import com.example.shortener.application.port.in.CreateShortUrlUseCase;
import com.example.shortener.application.port.out.CachePort;
import com.example.shortener.application.port.out.IdGenerator;
import com.example.shortener.application.port.out.UrlRepositoryPort;
import com.example.shortener.common.exception.ValidationException;
import com.example.shortener.common.util.Base62Encoder;
import com.example.shortener.domain.model.ShortUrl;
import com.example.shortener.domain.valueobject.LongUrl;
import com.example.shortener.domain.valueobject.ShortCode;

import java.time.Instant;

public class ShortenUrlService implements CreateShortUrlUseCase {

    private final IdGenerator idGenerator;
    private final UrlRepositoryPort urlRepository;
    private final CachePort cachePort;

    public ShortenUrlService(IdGenerator idGenerator,
                             UrlRepositoryPort urlRepository,
                             CachePort cachePort, Base62Encoder base62Encoder) {

        this.idGenerator = idGenerator;
        this.urlRepository = urlRepository;
        this.cachePort = cachePort;
    }


    @Override
    public ShortUrl createShortUrl(String longUrl) {
        validateLongUrl(longUrl);

        // Генерация id с помощью бастракции(её реализует snowflacke класс)
        Long genId = idGenerator.nextId();
        ShortUrl shortUrl = createShortCodeAndUrl(genId, longUrl);

        // Сохранение в КЕШ и БД
        cachePort.save(shortUrl.getShortCode(), longUrl);
        urlRepository.save(shortUrl);

        return shortUrl;
    }


    private ShortUrl createShortCodeAndUrl(Long id, String longUrl) {
        // Создание короткого url с помощью Base62(id -> цифроБуквенный код)
        ShortCode shortCode = ShortCode.of(Base62Encoder.encode(id));

        return ShortUrl.create(
                id,
                shortCode,
                LongUrl.of(longUrl)
        );
    }



    // Вспомогательные методы
    private void validateLongUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new ValidationException("URL cannot be empty");
        }
        if (url.length() > 2048) {
            throw new ValidationException("URL is too long");
        }
    }
}
