package com.metropol.credit.models.requests;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class LoanApplicationRequest {

    @NotNull(message = "Customer ID is required")
    Long customerId;

    @NotNull(message = "Amount requested is required")
    @DecimalMin(value = "0.01", inclusive = false, message = "Amount requested must be greater than zero")
    @Digits(integer = 12, fraction = 2, message = "Invalid amount format")
    BigDecimal amountRequested;

    @NotBlank(message = "Purpose is required")
    @Size(max = 255, message = "Purpose cannot exceed 255 characters")
    String purpose;

    @NotNull(message = "Loan term in months is required")
    @Min(value = 1, message = "Loan term must be at least 1 month")
    @Max(value = 120, message = "Loan term cannot exceed 120 months")
    Integer termInMonths;

}