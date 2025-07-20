package com.vicgroup.veterinaria.modules.user.dto.response;

/* src/main/java/com/vicgroup/veterinaria/modules/pet/dto/detail/OwnerDetailDto.java */

import com.vicgroup.veterinaria.modules.user.model.OwnerProfile;
import lombok.Data;

@Data
public class OwnerDetailDto {
    private Long id;          // id (PK) de OwnerProfile
    private String fullName;  // o username
    private String email;
    private String phone;

    public static OwnerDetailDto from(OwnerProfile op) {
        OwnerDetailDto d = new OwnerDetailDto();
        d.setId(op.getId());
        d.setFullName(op.getUser().getUsername());
        d.setEmail(op.getEmail());
        d.setPhone(op.getCelNum());
        return d;
    }
}
