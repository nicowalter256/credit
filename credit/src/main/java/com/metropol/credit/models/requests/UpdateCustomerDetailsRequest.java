package com.metropol.credit.models.requests;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class UpdateCustomerDetailsRequest {

    @Email(message = "Invalid email format")
    String email;

    @Pattern(regexp = "^[\\d\\s\\-+()]{8,}$", message = "Invalid phone number format")
    String phone;

    String address;

}