package com.metropol.credit.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class LoanRepaymentDTO {
    Long id;
    Long loanId;
    BigDecimal amountPaid;
    ZonedDateTime paymentDate;
    String paymentMethod;
}