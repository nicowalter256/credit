package com.metropol.credit.dtos;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class UserLoginDto {

    String firstName;
    String lastName;
    String phoneNumber;
    String email;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;
    Boolean isActive;
    String accessToken;
    String refreshToken;
    ZonedDateTime refreshTokenExpires;
}
