package com.example.shortener.application.port.out;

import java.time.Duration;
import java.util.Optional;

public interface CachePort {
    void save(String shortCode, String longUrl);

    Optional<String> get(String shortCode);

    boolean delete(String shortCode);

    boolean exists(String shortCode);
}
