package com.metropol.credit.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustomerDTO {
    Long id;
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
    String smeName;
    LocalDate dateOfBirth;
    String smeRegistrationNumber;
}