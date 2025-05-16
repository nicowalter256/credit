package com.metropol.credit.models.requests;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class UpdateLoanBalanceRequest {

    @NotNull(message = "New balance is required")
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    @Digits(integer = 12, fraction = 2, message = "Invalid amount format for balance")
    private BigDecimal newBalance;
}