package com.metropol.credit.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.metropol.credit.interfaces.LoanRepaymentService;
import com.metropol.credit.models.entities.Loan;
import com.metropol.credit.models.entities.LoanRepayment;
import com.metropol.credit.models.requests.RecordRepaymentRequest;
import com.metropol.credit.repositories.LoanRepaymentRepository;

@Service
public class LoanRepaymentServiceImpl implements LoanRepaymentService {

    @Autowired
    LoanRepaymentRepository loanRepaymentRepository;

    @Async
    @Override
    public CompletableFuture<LoanRepayment> recordRepayment(RecordRepaymentRequest repaymentRequest) {

        Loan loan = new Loan();
        loan.setId(repaymentRequest.getLoanId());

        LoanRepayment newRepayment = new LoanRepayment();
        newRepayment.setLoan(loan);
        newRepayment.setAmountPaid(repaymentRequest.getAmountPaid());
        newRepayment.setPaymentMethod(repaymentRequest.getPaymentMethod());
        newRepayment.setPaymentDate(ZonedDateTime.now());

        loanRepaymentRepository.save(newRepayment);
        return CompletableFuture.completedFuture(newRepayment);
    }

    @Override
    public Optional<LoanRepayment> findRepaymentById(Long repaymentId) {

        return loanRepaymentRepository.findById(repaymentId);
    }

    @Override
    public List<LoanRepayment> getRepaymentsForLoan(Long loanId) {

        Loan loan = new Loan();
        loan.setId(loanId);

        return loanRepaymentRepository.findByLoan(loan);
    }

    @Override
    public List<LoanRepayment> getRepaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return loanRepaymentRepository.findByPaymentDateBetween(ZonedDateTime.of(startDate, ZoneId.of("UTC")),
                ZonedDateTime.of(endDate, ZoneId.of("UTC")));
    }
}