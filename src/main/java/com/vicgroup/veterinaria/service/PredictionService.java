package com.vicgroup.veterinaria.service;

//package com.vicgroup.veterinaria.service;

import java.util.List;
import java.util.Map;

import com.vicgroup.veterinaria.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
public class PredictionService {

    private final WebClient web = WebClient.builder()
            .baseUrl("http://localhost:5000/")   // nombre de servicio en docker-compose
            .build();

    public PredictionResponse predict(List<String> symptoms) {
        return web.post()
                .uri("/predict")
                .bodyValue(Map.of("symptoms", symptoms))
                .retrieve()
                .bodyToMono(PredictionResponse.class)
                .block();
    }
}
