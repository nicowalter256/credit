package com.metropol.credit.unitTests;

import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.entities.Loan;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.enums.LoanApplicationStatus;
import com.metropol.credit.models.enums.LoanStatus;
import com.metropol.credit.models.requests.UpdateLoanStatusRequest;
import com.metropol.credit.repositories.CustomerRepository;
import com.metropol.credit.repositories.LoanRepository;
import com.metropol.credit.services.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceImplUnitTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Customer customer;
    private LoanApplication approvedApplication;
    private Loan loan;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);

        approvedApplication = new LoanApplication();
        approvedApplication.setId(1L);
        approvedApplication.setCustomer(customer);
        approvedApplication.setStatus(LoanApplicationStatus.APPROVED);
        approvedApplication.setAmountRequested(new BigDecimal("10000"));

        loan = new Loan();
        loan.setId(1L);
        loan.setLoanApplication(approvedApplication);
        loan.setAmountApproved(new BigDecimal("10000"));
        loan.setOutstandingBalance(new BigDecimal("10000"));
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setInterestRate(new BigDecimal("0.10"));
        loan.setTermInMonths(12);
        loan.setStartDate(LocalDate.now());
        loan.setEndDate(LocalDate.now().plusMonths(12));
    }

    @Test
    void createLoanFromApplication_Success() throws ExecutionException, InterruptedException {
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        CompletableFuture<Loan> future = loanService.createLoanFromApplication(approvedApplication);
        Loan result = future.get();

        assertNotNull(result);
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
        assertEquals(approvedApplication.getAmountRequested(), result.getAmountApproved());
        assertEquals(approvedApplication, result.getLoanApplication());
        assertNotNull(result.getLoanApplication().getLoan()); // Check bidirectional link
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void createLoanFromApplication_NotApproved_ThrowsException() {
        approvedApplication.setStatus(LoanApplicationStatus.PENDING);
        assertThrows(IllegalArgumentException.class, () -> loanService.createLoanFromApplication(approvedApplication));
    }

    @Test
    void createLoanFromApplication_LoanAlreadyExists_ThrowsException() {
        approvedApplication.setLoan(new Loan()); // Link an existing loan
        assertThrows(IllegalArgumentException.class, () -> loanService.createLoanFromApplication(approvedApplication));
    }

    @Test
    void findLoanById_Found_Success() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        Optional<Loan> result = loanService.findLoanById(1L);
        assertTrue(result.isPresent());
        assertEquals(loan, result.get());
    }

    @Test
    void getLoansByCustomerId_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(loanRepository.findByCustomer(customer)).thenReturn(Collections.singletonList(loan));

        List<Loan> results = loanService.getLoansByCustomerId(1L);

        assertFalse(results.isEmpty());
        assertEquals(loan, results.get(0));
    }

    @Test
    void getLoansByCustomerId_CustomerNotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> loanService.getLoansByCustomerId(1L));
    }

    @Test
    void getLoansByStatus_Success() {
        when(loanRepository.findByStatus(LoanStatus.ACTIVE)).thenReturn(Collections.singletonList(loan));
        List<Loan> results = loanService.getLoansByStatus(LoanStatus.ACTIVE);
        assertFalse(results.isEmpty());
        assertEquals(loan, results.get(0));
    }

    @Test
    void updateLoanStatus_Success() throws ExecutionException, InterruptedException {
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setNewStatus(LoanStatus.PAID_OFF);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        CompletableFuture<Loan> future = loanService.updateLoanStatus(1L, request);
        Loan result = future.get();

        assertEquals(LoanStatus.PAID_OFF, result.getStatus());
        verify(loanRepository).save(loan);
    }

    @Test
    void updateLoanStatus_LoanNotFound_ThrowsException() {
        UpdateLoanStatusRequest request = new UpdateLoanStatusRequest();
        request.setNewStatus(LoanStatus.PAID_OFF);
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> loanService.updateLoanStatus(1L, request).get());
    }

    @Test
    void updateOutstandingBalance_Success() {
        BigDecimal newBalance = new BigDecimal("5000");
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan result = loanService.updateOutstandingBalance(1L, newBalance);

        assertEquals(newBalance, result.getOutstandingBalance());
        verify(loanRepository).save(loan);
    }

    @Test
    void findOverdueLoans_Success() {
        when(loanRepository.findOverdueLoansByStatus(LoanStatus.ACTIVE, LocalDate.now()))
                .thenReturn(Collections.singletonList(loan));
        List<Loan> results = loanService.findOverdueLoans();
        assertFalse(results.isEmpty());
        assertEquals(loan, results.get(0));
    }
}