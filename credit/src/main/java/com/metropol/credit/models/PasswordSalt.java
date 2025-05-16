package com.metropol.credit.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordSalt {
    String password;
    String salt;
}
