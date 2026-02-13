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
import com.example.shortener.infrastructure.persistence.entity.UrlEntity;
import com.example.shortener.infrastructure.persistence.mapper.UrlMapper;
import com.example.shortener.infrastructure.persistence.repository.JpaUrlRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ShortenUrlService implements CreateShortUrlUseCase {

    private final IdGenerator idGenerator;
    private final UrlRepositoryPort urlRepository;
    private final CachePort cachePort;
    private final JpaUrlRepository jpaUrlRepository;
    private final UrlMapper urlMapper;

    public ShortenUrlService(IdGenerator idGenerator,
                             UrlRepositoryPort urlRepository,
                             CachePort cachePort, JpaUrlRepository jpaUrlRepository, UrlMapper urlMapper) {

        this.idGenerator = idGenerator;
        this.urlRepository = urlRepository;
        this.cachePort = cachePort;
        this.jpaUrlRepository = jpaUrlRepository;
        this.urlMapper = urlMapper;
    }




    @Override
    public ShortUrl createShortUrl(String longUrl) {
        validateLongUrl(longUrl);
        UrlEntity urlEntity = urlRepository.findByLongUrl(longUrl);

        if(urlEntity != null && urlEntity.getExpiresAt().isAfter(Instant.now())) {
            // Обновление существующей записи(обновление записи в кеше
            // , обновление срока действия в БД и сохранение в БД)
            updateExistingLongUrl(urlEntity);
            return urlMapper.toDomain(urlEntity);


        } else {
            // Генерация id с помощью абстракции(её реализует snowflacke класс)
            Long genId = idGenerator.nextId();
            ShortUrl shortUrl = createShortCodeAndUrl(genId, longUrl);

            if(urlRepository.existsByShortCode(shortUrl.getShortCode())) {
                throw new ValidationException("Этот ShortCode уже существует " + shortUrl.getShortCode());
            }

            // Сохранение в КЕШ и БД
            urlRepository.save(shortUrl);
            cachePort.save(shortUrl.getShortCode(), longUrl);

            return shortUrl;
        }
    }




    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ

    private void updateExistingLongUrl(UrlEntity urlEntity) {
        cachePort.save(urlEntity.getShortCode(), urlEntity.getLongUrl());
        urlEntity.setExpiresAt(Instant.now());
        jpaUrlRepository.save(urlEntity);
    }

    //СОздание короткого URL
    private ShortUrl createShortCodeAndUrl(Long id, String longUrl) {
        // Создание shortCode(Часть shortUrl) с помощью Base62(id -> цифроБуквенный код)
        ShortCode shortCode = ShortCode.of(Base62Encoder.encode(id));

        return ShortUrl.create(
                id,
                shortCode,
                LongUrl.of(longUrl)
        );
    }

    //Валидация строки URL на разумные размеры
    private void validateLongUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new ValidationException("URL cannot be empty");
        }
        if (url.length() > 2048) {
            throw new ValidationException("URL is too long");
        }
    }
}
