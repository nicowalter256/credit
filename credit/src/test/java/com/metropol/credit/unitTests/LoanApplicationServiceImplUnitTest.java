package com.metropol.credit.unitTests;

import com.metropol.credit.interfaces.LoanService;
import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.enums.LoanApplicationStatus;
import com.metropol.credit.models.requests.LoanApplicationRequest;
import com.metropol.credit.repositories.CustomerRepository;
import com.metropol.credit.repositories.LoanApplicationRepository;
import com.metropol.credit.services.LoanApplicationServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanApplicationServiceImplUnitTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanApplicationServiceImpl loanApplicationService;

    private Customer customer;
    private LoanApplication loanApplication;
    private LoanApplicationRequest applicationRequest;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);

        applicationRequest = new LoanApplicationRequest();
        applicationRequest.setCustomerId(1L);
        applicationRequest.setAmountRequested(new BigDecimal("5000"));
        applicationRequest.setPurpose("Vacation");

        loanApplication = new LoanApplication();
        loanApplication.setId(1L);
        loanApplication.setCustomer(customer);
        loanApplication.setAmountRequested(applicationRequest.getAmountRequested());
        loanApplication.setPurpose(applicationRequest.getPurpose());
        loanApplication.setStatus(LoanApplicationStatus.PENDING);
        loanApplication.setApplicationDate(ZonedDateTime.now());
    }

    @Test
    void submitLoanApplication_Success() throws ExecutionException, InterruptedException {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApplication);

        CompletableFuture<LoanApplication> future = loanApplicationService.submitLoanApplication(applicationRequest);
        LoanApplication result = future.get();

        assertNotNull(result);
        assertEquals(LoanApplicationStatus.PENDING, result.getStatus());
        assertEquals(customer, result.getCustomer());
        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    void submitLoanApplication_CustomerNotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            loanApplicationService.submitLoanApplication(applicationRequest).get();
        });
    }

    @Test
    void findLoanApplicationById_Found_Success() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));
        Optional<LoanApplication> result = loanApplicationService.findLoanApplicationById(1L);
        assertTrue(result.isPresent());
        assertEquals(loanApplication, result.get());
    }

    @Test
    void findLoanApplicationById_NotFound_ReturnsEmpty() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<LoanApplication> result = loanApplicationService.findLoanApplicationById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void getLoanApplicationsByCustomerId_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(loanApplicationRepository.findByCustomer(customer)).thenReturn(Collections.singletonList(loanApplication));

        List<LoanApplication> results = loanApplicationService.getLoanApplicationsByCustomerId(1L);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(loanApplication, results.get(0));
    }

    @Test
    void getLoanApplicationsByCustomerId_CustomerNotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> loanApplicationService.getLoanApplicationsByCustomerId(1L));
    }

    @Test
    void getLoanApplicationsByStatus_Success() {
        when(loanApplicationRepository.findByStatus(LoanApplicationStatus.PENDING))
                .thenReturn(Collections.singletonList(loanApplication));
        List<LoanApplication> results = loanApplicationService
                .getLoanApplicationsByStatus(LoanApplicationStatus.PENDING);
        assertFalse(results.isEmpty());
        assertEquals(loanApplication, results.get(0));
    }

    @Test
    void approveLoanApplication_Success() throws ExecutionException, InterruptedException, Exception {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loanApplication);

        CompletableFuture<LoanApplication> future = loanApplicationService.approveLoanApplication(1L);
        LoanApplication result = future.get();

        assertEquals(LoanApplicationStatus.APPROVED, result.getStatus());
        assertNotNull(result.getDecisionDate());
        verify(loanApplicationRepository).save(loanApplication);
    }

    @Test
    void approveLoanApplication_NotFound_ThrowsException() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> loanApplicationService.approveLoanApplication(1L).get());
    }

    @Test
    void approveLoanApplication_NotPending_ThrowsException() {
        loanApplication.setStatus(LoanApplicationStatus.APPROVED);
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(loanApplication));

        assertThrows(IllegalArgumentException.class, () -> loanApplicationService.approveLoanApplication(1L).get());
    }

    @Test
    void rejectLoanApplication_Success() throws ExecutionException, InterruptedException {
        String reason = "Bad credit";
        CompletableFuture<LoanApplication> future = loanApplicationService.rejectLoanApplication(1L, reason);
        LoanApplication result = future.get();

        assertEquals(1L, result.getId());
        assertEquals(LoanApplicationStatus.REJECTED, result.getStatus());
        assertEquals(reason, result.getRejectionReason());
    }

    @Test
    void cancelLoanApplication_Success() throws ExecutionException, InterruptedException {
        CompletableFuture<LoanApplication> future = loanApplicationService.cancelLoanApplication(1L);
        LoanApplication result = future.get();

        assertEquals(1L, result.getId());
        assertEquals(LoanApplicationStatus.CANCELED, result.getStatus());
    }
}