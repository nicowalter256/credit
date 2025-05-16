package com.metropol.credit.models.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UserLogin {
    @NotBlank
    @Email(message = "email must be valid", flags = Pattern.Flag.CASE_INSENSITIVE)
    String email;
    @NotBlank
    @Size(min = 7, message = "minimum allowed characters are 7")
    String password;

    public UserLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
