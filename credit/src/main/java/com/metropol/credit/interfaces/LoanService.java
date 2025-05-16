package com.metropol.credit.interfaces;

import com.metropol.credit.models.entities.Loan;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.enums.LoanStatus;
import com.metropol.credit.models.requests.UpdateLoanStatusRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface LoanService {

    CompletableFuture<Loan> createLoanFromApplication(LoanApplication approvedApplication);

    Optional<Loan> findLoanById(Long loanId);

    List<Loan> getLoansByCustomerId(Long customerId);

    List<Loan> getLoansByStatus(LoanStatus status);

    CompletableFuture<Loan> updateLoanStatus(Long loanId, UpdateLoanStatusRequest statusRequest);

    Loan updateOutstandingBalance(Long loanId, BigDecimal newBalance);

    List<Loan> findOverdueLoans();

}