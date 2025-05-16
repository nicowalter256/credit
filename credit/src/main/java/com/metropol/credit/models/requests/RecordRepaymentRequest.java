package com.metropol.credit.models.requests;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class RecordRepaymentRequest {

    @NotNull(message = "Loan ID is required")
    Long loanId;

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.01", inclusive = false, message = "Amount paid must be greater than zero")
    @Digits(integer = 12, fraction = 2, message = "Invalid amount format")
    BigDecimal amountPaid;

    @NotBlank(message = "Payment method is required")
    String paymentMethod;

}