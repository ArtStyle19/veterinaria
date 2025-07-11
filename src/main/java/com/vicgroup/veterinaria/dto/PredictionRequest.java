package com.vicgroup.veterinaria.dto;

import java.util.List;

public record PredictionRequest(List<String> symptoms) {}