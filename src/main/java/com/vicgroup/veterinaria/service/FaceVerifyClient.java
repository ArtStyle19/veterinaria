package com.vicgroup.veterinaria.service;

import org.springframework.stereotype.Service; // Marks this class as a Spring service component
import org.springframework.web.reactive.function.client.WebClient; // Reactive HTTP client from Spring WebFlux
//import org.springframework.web.function.client.WebClient; // Reactive HTTP client from Spring WebFlux

import java.util.Base64; // For Base64 encoding/decoding operations
import java.util.Map;    // For creating the request body as a Map

/**
 * Client service for interacting with an external face verification API.
 * This service uses Spring WebClient to send face probe images (Base64 encoded)
 * and reference vectors to a remote service for verification.
 */


@Service
public class FaceVerifyClient {

    private final WebClient web = WebClient.create("http://localhost:5000");

    public boolean verify(String probeB64, byte[] refVec) {
        String refB64 = Base64.getEncoder().encodeToString(refVec);
        Map<String, Object> body = Map.of("probe", probeB64,
                "reference", refB64);

        return Boolean.TRUE.equals(web.post().uri("/verify").bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(m -> (Boolean) m.get("match"))
                .defaultIfEmpty(false)
                .block());
//        return web.post().uri("/verify").bodyValue(body)
//                .retrieve()
//                .bodyToMono(Map.class)
//                .map(m -> (Boolean) m.get("match"))
//                .defaultIfEmpty(false)
//                .block();
    }
}
