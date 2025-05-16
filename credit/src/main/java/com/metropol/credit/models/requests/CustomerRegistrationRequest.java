package com.metropol.credit.models.requests;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustomerRegistrationRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password;

    @NotBlank(message = "Sme name is required")
    String smeName;

    @NotBlank(message = "Sme registration number is required")
    String smeRegistrationNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[\\d\\s\\-+()]{8,}$", message = "Invalid phone number format")
    String phone;

    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth;

    @NotBlank(message = "Address is required")
    String address;

}