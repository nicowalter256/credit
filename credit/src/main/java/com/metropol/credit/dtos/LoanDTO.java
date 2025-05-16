package com.metropol.credit.dtos;

import com.metropol.credit.models.enums.LoanStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanDTO {
    private Long id;
    private Long loanApplicationId;
    private BigDecimal amountApproved;
    private BigDecimal interestRate;
    private Integer termInMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal outstandingBalance;
    private String status;
}