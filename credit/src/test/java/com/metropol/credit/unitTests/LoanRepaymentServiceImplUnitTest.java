package com.metropol.credit.unitTests;

import com.metropol.credit.models.entities.Loan;
import com.metropol.credit.models.entities.LoanRepayment;
import com.metropol.credit.models.requests.RecordRepaymentRequest;
import com.metropol.credit.repositories.LoanRepaymentRepository;
import com.metropol.credit.repositories.LoanRepository;
import com.metropol.credit.services.LoanRepaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanRepaymentServiceImplUnitTest {

    @Mock
    private LoanRepaymentRepository loanRepaymentRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanRepaymentServiceImpl loanRepaymentService;

    private Loan loan;
    private LoanRepayment loanRepayment;
    private RecordRepaymentRequest repaymentRequest;

    @BeforeEach
    void setUp() {
        loan = new Loan();
        loan.setId(1L);
        loan.setOutstandingBalance(new BigDecimal("1000"));

        repaymentRequest = new RecordRepaymentRequest();
        repaymentRequest.setLoanId(1L);
        repaymentRequest.setAmountPaid(new BigDecimal("100"));
        repaymentRequest.setPaymentMethod("TRANSFER");

        loanRepayment = new LoanRepayment();
        loanRepayment.setId(1L);
        loanRepayment.setLoan(loan);
        loanRepayment.setAmountPaid(repaymentRequest.getAmountPaid());
        loanRepayment.setPaymentMethod(repaymentRequest.getPaymentMethod());
        loanRepayment.setPaymentDate(ZonedDateTime.now());
    }

    @Test
    void recordRepayment_Success() throws ExecutionException, InterruptedException {
        CompletableFuture<LoanRepayment> future = loanRepaymentService.recordRepayment(repaymentRequest);
        LoanRepayment result = future.get();

        assertNotNull(result);
        assertEquals(repaymentRequest.getAmountPaid(), result.getAmountPaid());
        assertEquals(repaymentRequest.getPaymentMethod(), result.getPaymentMethod());
    }

    @Test
    void findRepaymentById_Found_Success() {
        Optional<LoanRepayment> result = loanRepaymentService.findRepaymentById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void getRepaymentsForLoan_Success() {
        List<LoanRepayment> results = loanRepaymentService.getRepaymentsForLoan(1L);
        assertTrue(results.isEmpty());
    }

    @Test
    void getRepaymentsByDateRange_Success() {

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<LoanRepayment> results = loanRepaymentService.getRepaymentsByDateRange(start, end);
        assertTrue(results.isEmpty());
    }
}