package com.vicgroup.veterinaria.modules.prediction.dto.response;

import java.util.Map;

public record PredictionResponse(String disease, Map<String,String> precautions) {}