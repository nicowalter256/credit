package com.metropol.credit.services;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.metropol.credit.configurations.CustomException;
import com.metropol.credit.interfaces.LoanApplicationService;
import com.metropol.credit.interfaces.LoanService;
import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.enums.LoanApplicationStatus;
import com.metropol.credit.models.requests.LoanApplicationRequest;
import com.metropol.credit.repositories.CustomerRepository;
import com.metropol.credit.repositories.LoanApplicationRepository;

@Service
public class LoanApplicationServiceImpl implements LoanApplicationService {

    @Autowired
    LoanApplicationRepository loanApplicationRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    LoanService loanService;

    @Async
    @Override
    public CompletableFuture<LoanApplication> submitLoanApplication(LoanApplicationRequest applicationRequest) {

        Customer customer = customerRepository.findById(applicationRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException(
                        "Customer not found with ID: " + applicationRequest.getCustomerId()));

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setCustomer(customer);
        loanApplication.setAmountRequested(applicationRequest.getAmountRequested());
        loanApplication.setPurpose(applicationRequest.getPurpose());
        loanApplication.setTermInMonths(applicationRequest.getTermInMonths());
        loanApplication.setStatus(LoanApplicationStatus.PENDING);
        loanApplication.setApplicationDate(ZonedDateTime.now());

        LoanApplication savedApplication = loanApplicationRepository.save(loanApplication);

        return CompletableFuture.completedFuture(savedApplication);
    }

    @Override
    public Optional<LoanApplication> findLoanApplicationById(Long applicationId) {

        return loanApplicationRepository.findById(applicationId);
    }

    @Override
    public List<LoanApplication> getLoanApplicationsByCustomerId(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        return loanApplicationRepository.findByCustomer(customer);
    }

    @Override
    public List<LoanApplication> getLoanApplicationsByStatus(LoanApplicationStatus status) {

        return loanApplicationRepository.findByStatus(status);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER', 'LOAN_OFFICER')")
    @Async
    @Override
    public CompletableFuture<LoanApplication> approveLoanApplication(Long applicationId) throws Exception {

        LoanApplication application = findLoanApplicationById(applicationId)
                .orElseThrow(() -> new RuntimeException("Loan application not found with ID: " + applicationId));

        if (application.getStatus() != LoanApplicationStatus.PENDING) {
            throw new CustomException("Loan application is not in PENDING status and cannot be approved.",
                    HttpStatus.BAD_REQUEST);
        }

        application.setStatus(LoanApplicationStatus.APPROVED);
        application.setDecisionDate(ZonedDateTime.now());

        loanApplicationRepository.save(application);

        loanService.createLoanFromApplication(application);

        return CompletableFuture.completedFuture(application);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER', 'LOAN_OFFICER')")
    @Async
    @Override
    public CompletableFuture<LoanApplication> rejectLoanApplication(Long applicationId, String reason) {

        LoanApplication application = findLoanApplicationById(applicationId).get();
        application.setStatus(LoanApplicationStatus.REJECTED);
        application.setRejectionReason(reason);
        return CompletableFuture.completedFuture(application);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER', 'LOAN_OFFICER')")
    @Async
    @Override
    public CompletableFuture<LoanApplication> cancelLoanApplication(Long applicationId) {

        LoanApplication application = findLoanApplicationById(applicationId).get();
        application.setStatus(LoanApplicationStatus.CANCELED);
        return CompletableFuture.completedFuture(application);
    }
}