package com.vicgroup.veterinaria.modules.prediction.dto.request;

import java.util.List;

public record PredictionRequest(List<String> symptoms) {}