package com.metropol.credit.services;

import com.metropol.credit.configurations.CustomException;
import com.metropol.credit.interfaces.LoanService;
import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.enums.LoanApplicationStatus;
import com.metropol.credit.models.requests.LoanApplicationRequest;
import com.metropol.credit.repositories.CustomerRepository;
import com.metropol.credit.repositories.LoanApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceImplTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanApplicationServiceImpl loanApplicationService;

    @Test
    void submitLoanApplication_shouldSaveApplicationWithTermInMonths() throws ExecutionException, InterruptedException {

        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setCustomerId(1L);
        request.setAmountRequested(new BigDecimal("1000.00"));
        request.setPurpose("Test loan");
        request.setTermInMonths(24);

        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenAnswer(invocation -> {
            LoanApplication appToSave = invocation.getArgument(0);
            appToSave.setId(100L);
            assertEquals(request.getTermInMonths(), appToSave.getTermInMonths());
            return appToSave;
        });

        CompletableFuture<LoanApplication> futureResult = loanApplicationService.submitLoanApplication(request);
        LoanApplication result = futureResult.get();

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(request.getTermInMonths(), result.getTermInMonths());
        assertEquals(LoanApplicationStatus.PENDING, result.getStatus());
        verify(customerRepository).findById(1L);
        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    void approveLoanApplication_shouldSetStatusAndCallLoanServiceWithCorrectApplication() throws Exception {

        Long applicationId = 1L;
        LoanApplication pendingApplication = new LoanApplication();
        pendingApplication.setId(applicationId);
        pendingApplication.setStatus(LoanApplicationStatus.PENDING);
        pendingApplication.setAmountRequested(new BigDecimal("5000"));
        pendingApplication.setTermInMonths(18);

        Customer customer = new Customer();
        customer.setId(1L);
        pendingApplication.setCustomer(customer);

        when(loanApplicationRepository.findById(applicationId)).thenReturn(Optional.of(pendingApplication));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(pendingApplication);
        when(loanService.createLoanFromApplication(any(LoanApplication.class)))
                .thenReturn(CompletableFuture.completedFuture(null));
        CompletableFuture<LoanApplication> futureResult = loanApplicationService.approveLoanApplication(applicationId);
        LoanApplication approvedApplication = futureResult.get();

        assertNotNull(approvedApplication);
        assertEquals(LoanApplicationStatus.APPROVED, approvedApplication.getStatus());
        assertNotNull(approvedApplication.getDecisionDate());
        assertEquals(18, approvedApplication.getTermInMonths());
        verify(loanApplicationRepository).findById(applicationId);
        verify(loanApplicationRepository).save(approvedApplication);
        verify(loanService).createLoanFromApplication(
                argThat(app -> app.getId().equals(applicationId) && app.getTermInMonths().equals(18)));
    }
}