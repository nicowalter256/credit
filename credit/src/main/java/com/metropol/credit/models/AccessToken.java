package com.metropol.credit.models;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class AccessToken {
    String accessToken;
    String refreshToken;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS'Z'")
    ZonedDateTime refreshTokenExpires;

    public AccessToken(String accessToken, String refreshToken, ZonedDateTime refreshTokenExpires) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshTokenExpires = refreshTokenExpires;
    }

}
