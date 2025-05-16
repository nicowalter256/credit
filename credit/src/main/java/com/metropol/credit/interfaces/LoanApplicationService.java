package com.metropol.credit.interfaces;

import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.enums.LoanApplicationStatus;
import com.metropol.credit.models.requests.LoanApplicationRequest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface LoanApplicationService {

    CompletableFuture<LoanApplication> submitLoanApplication(LoanApplicationRequest applicationRequest);

    Optional<LoanApplication> findLoanApplicationById(Long applicationId);

    List<LoanApplication> getLoanApplicationsByCustomerId(Long customerId);

    List<LoanApplication> getLoanApplicationsByStatus(LoanApplicationStatus status);

    CompletableFuture<LoanApplication> approveLoanApplication(Long applicationId) throws Exception;

    CompletableFuture<LoanApplication> rejectLoanApplication(Long applicationId, String reason);

    CompletableFuture<LoanApplication> cancelLoanApplication(Long applicationId);

}