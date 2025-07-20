package com.vicgroup.veterinaria.modules.user.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String roleName;
}
