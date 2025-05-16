package com.metropol.credit.services;

import com.metropol.credit.interfaces.LoanService;
import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.entities.Loan;
import com.metropol.credit.repositories.CustomerRepository;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.enums.LoanApplicationStatus;
import com.metropol.credit.models.enums.LoanStatus;
import com.metropol.credit.repositories.LoanRepository;
import com.metropol.credit.models.requests.UpdateLoanStatusRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER', 'LOAN_OFFICER')")
    @Async
    @Override
    public CompletableFuture<Loan> createLoanFromApplication(LoanApplication approvedApplication) {

        if (approvedApplication.getStatus() != LoanApplicationStatus.APPROVED) {
            throw new IllegalArgumentException("Loan can only be created from an APPROVED application.");
        }
        if (approvedApplication.getLoan() != null) {
            throw new IllegalArgumentException(
                    "Loan already exists for application ID: " + approvedApplication.getId());
        }

        Loan loan = new Loan();
        loan.setLoanApplication(approvedApplication);
        loan.setAmountApproved(approvedApplication.getAmountRequested());

        Integer term = approvedApplication.getTermInMonths() != null ? approvedApplication.getTermInMonths() : 12;

        loan.setInterestRate(new BigDecimal("0.10"));
        loan.setTermInMonths(term);
        loan.setStartDate(LocalDate.now());
        loan.setEndDate(LocalDate.now().plusMonths(term));
        loan.setOutstandingBalance(approvedApplication.getAmountRequested());
        loan.setStatus(LoanStatus.ACTIVE);
        Loan savedLoan = loanRepository.save(loan);

        approvedApplication.setLoan(savedLoan);

        return CompletableFuture.completedFuture(savedLoan);
    }

    @Override
    public Optional<Loan> findLoanById(Long loanId) {
        return loanRepository.findById(loanId);
    }

    @Override
    public List<Loan> getLoansByCustomerId(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        return loanRepository.findByCustomer(customer);
    }

    @Override
    public List<Loan> getLoansByStatus(LoanStatus status) {
        return loanRepository.findByStatus(status);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER', 'LOAN_OFFICER')")
    @Async
    @Override
    public CompletableFuture<Loan> updateLoanStatus(Long loanId, UpdateLoanStatusRequest statusRequest) {

        Loan loan = findLoanById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));

        loan.setStatus(statusRequest.getNewStatus());
        return CompletableFuture.completedFuture(loanRepository.save(loan));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER', 'LOAN_OFFICER')")
    @Override
    public Loan updateOutstandingBalance(Long loanId, BigDecimal newBalance) {

        Loan loan = findLoanById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));
        loan.setOutstandingBalance(newBalance);
        return loanRepository.save(loan);
    }

    @Override
    public List<Loan> findOverdueLoans() {

        return loanRepository.findOverdueLoansByStatus(LoanStatus.ACTIVE, LocalDate.now());
    }
}