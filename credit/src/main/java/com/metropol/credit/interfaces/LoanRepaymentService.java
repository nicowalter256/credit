package com.metropol.credit.interfaces;

import com.metropol.credit.models.requests.RecordRepaymentRequest;
import com.metropol.credit.models.entities.LoanRepayment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface LoanRepaymentService {

    CompletableFuture<LoanRepayment> recordRepayment(RecordRepaymentRequest repaymentRequest);

    Optional<LoanRepayment> findRepaymentById(Long repaymentId);

    List<LoanRepayment> getRepaymentsForLoan(Long loanId);

    List<LoanRepayment> getRepaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

}