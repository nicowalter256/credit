package com.metropol.credit.dtos;

import com.metropol.credit.models.enums.SystemUserRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class SystemUserDTO {
    Long id;
    String email;
    String firstName;
    String lastName;
    SystemUserRole role;
    boolean isActive;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;
}