package com.metropol.credit.models.requests;

import com.metropol.credit.models.enums.SystemUserRole;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CreateSystemUserRequest {

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    String email;

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    String lastName;

    @NotNull(message = "Role is mandatory")
    SystemUserRole role;

}