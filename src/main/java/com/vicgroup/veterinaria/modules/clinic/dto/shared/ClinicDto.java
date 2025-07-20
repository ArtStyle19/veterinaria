package com.vicgroup.veterinaria.modules.clinic.dto.shared;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import com.vicgroup.veterinaria.modules.clinic.model.Clinic;
import java.math.BigDecimal;

@Data
public class ClinicDto {
    private Long id;                       // returned to the client

    @NotBlank
    private String name;

    private String address;

    @NotNull
    private BigDecimal latitude;

    @NotNull
    private BigDecimal longitude;

    private String email;

    /* -------- mapping helpers -------- */
    public static ClinicDto fromEntity(Clinic c) {
        ClinicDto d = new ClinicDto();
        d.setId(c.getId());
        d.setName(c.getName());
        d.setAddress(c.getAddress());
        d.setLatitude(c.getLatitude());
        d.setLongitude(c.getLongitude());
        d.setEmail(c.getEmail());
        return d;
    }

    public void toEntity(Clinic c) {
        c.setName(name);
        c.setAddress(address);
        c.setLatitude(latitude);
        c.setLongitude(longitude);
        c.setEmail(email);
    }
}
