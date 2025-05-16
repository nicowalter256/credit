package com.metropol.credit.models.requests;

import com.metropol.credit.models.enums.SystemUserRole;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UpdateSystemUserRequest {

    @Email(message = "Email should be valid")
    String email;

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    String lastName;

    SystemUserRole role;
}