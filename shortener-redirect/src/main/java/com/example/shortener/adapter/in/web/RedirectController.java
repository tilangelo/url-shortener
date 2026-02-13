package com.example.shortener.adapter.in.web;

import com.example.shortener.application.port.in.RedirectUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;

@RestController
public class RedirectController {
    private final RedirectUseCase redirectUseCase;

    public RedirectController(RedirectUseCase redirectUseCase) {
        this.redirectUseCase = redirectUseCase;
    }


    @GetMapping("/{code}")
    public Mono<ResponseEntity<Void>> redirect(@PathVariable String code) {
        return Mono.fromCallable(() -> redirectUseCase.redirect(code))
                .subscribeOn(Schedulers.boundedElastic()) // потому что внутри blocking JPA
                .map(url -> ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(url))
                        .build());
    }
}
