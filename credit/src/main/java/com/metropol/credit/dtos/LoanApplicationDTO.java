package com.metropol.credit.dtos;

import com.metropol.credit.models.enums.LoanApplicationStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LoanApplicationDTO {
    Long id;
    CustomerDTO customer;
    BigDecimal amountRequested;
    LoanApplicationStatus status;
    String purpose;
    LocalDateTime applicationDate;
    String rejectionReason;
}