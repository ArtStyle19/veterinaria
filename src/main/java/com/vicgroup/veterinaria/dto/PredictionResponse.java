package com.vicgroup.veterinaria.dto;

import java.util.Map;

public record PredictionResponse(String disease, Map<String,String> precautions) {}