package com.metropol.credit.models.requests;

import com.metropol.credit.models.enums.LoanStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateLoanStatusRequest {

    @NotNull(message = "New loan status is required")
    LoanStatus newStatus;

}