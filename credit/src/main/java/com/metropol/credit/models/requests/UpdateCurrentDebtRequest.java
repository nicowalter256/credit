package com.metropol.credit.models.requests;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Data
public class UpdateCurrentDebtRequest {

    @NotNull(message = "New current debt amount is required")
    @DecimalMin(value = "0.0", message = "Current debt cannot be negative")
    @Digits(integer = 12, fraction = 2, message = "Invalid amount format for current debt")
    private BigDecimal newDebtAmount;
}