package com.metropol.credit.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditProfileDTO {
    Long id;
    int creditScore;
    BigDecimal maxLoanAmount;
    BigDecimal currentDebt;

}