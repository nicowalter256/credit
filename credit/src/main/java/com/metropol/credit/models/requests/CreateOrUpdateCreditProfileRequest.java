package com.metropol.credit.models.requests;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreateOrUpdateCreditProfileRequest {

    @NotNull(message = "Customer ID is required")
    Long customerId;

    @NotNull(message = "Credit score is required")
    @Min(value = 300, message = "Credit score must be at least 300")
    @Max(value = 850, message = "Credit score must be at most 850")
    Integer creditScore;

    @NotNull(message = "Maximum loan amount is required")
    @DecimalMin(value = "0.0", message = "Maximum loan amount cannot be negative")
    @Digits(integer = 12, fraction = 2, message = "Invalid amount format for maximum loan")
    BigDecimal maxLoanAmount;

    @NotNull(message = "Current debt is required")
    @DecimalMin(value = "0.0", message = "Current debt cannot be negative")
    @Digits(integer = 12, fraction = 2, message = "Invalid amount format for current debt")
    BigDecimal currentDebt;
}