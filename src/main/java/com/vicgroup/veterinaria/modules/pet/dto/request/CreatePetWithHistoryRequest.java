package com.vicgroup.veterinaria.modules.pet.dto.request;

public class CreatePetWithHistoryRequest extends CreatePetRequest {
    public String ownerName;
    public String ownerContact;
    public HistoryData history;

    public static class HistoryData {
        public String description;
        public String treatment;
    }
}
