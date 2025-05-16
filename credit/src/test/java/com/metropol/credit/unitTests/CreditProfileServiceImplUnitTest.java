package com.metropol.credit.unitTests;

import com.metropol.credit.models.entities.CreditProfile;
import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.requests.CreateOrUpdateCreditProfileRequest;
import com.metropol.credit.models.requests.UpdateCreditScoreRequest;
import com.metropol.credit.models.requests.UpdateCurrentDebtRequest;
import com.metropol.credit.models.requests.UpdateMaxLoanAmountRequest;
import com.metropol.credit.repositories.CreditProfileRepository;
import com.metropol.credit.repositories.CustomerRepository;
import com.metropol.credit.services.CreditProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreditProfileServiceImplUnitTest {

    @Mock
    private CreditProfileRepository creditProfileRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CreditProfileServiceImpl creditProfileService;

    private Customer customer;
    private CreditProfile creditProfile;
    private CreateOrUpdateCreditProfileRequest createRequest;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@example.com");

        creditProfile = new CreditProfile();
        creditProfile.setId(1L);
        creditProfile.setCustomer(customer);
        creditProfile.setCreditScore(700);
        creditProfile.setMaxLoanAmount(new BigDecimal("10000"));
        creditProfile.setCurrentDebt(new BigDecimal("2000"));
        creditProfile.setLastAssessmentDate(ZonedDateTime.now());

        createRequest = new CreateOrUpdateCreditProfileRequest();
        createRequest.setCustomerId(1L);
        createRequest.setCreditScore(750);
        createRequest.setMaxLoanAmount(new BigDecimal("12000"));
        createRequest.setCurrentDebt(new BigDecimal("1500"));
    }

    @Test
    void createOrUpdateCreditProfile_NewProfile_Success() throws ExecutionException, InterruptedException {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(creditProfileRepository.findByCustomer(customer)).thenReturn(Optional.empty());
        when(creditProfileRepository.save(any(CreditProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CompletableFuture<CreditProfile> future = creditProfileService.createOrUpdateCreditProfile(createRequest);
        CreditProfile result = future.get();

        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        assertEquals(createRequest.getCreditScore(), result.getCreditScore());
        verify(creditProfileRepository).save(any(CreditProfile.class));
    }

    @Test
    void createOrUpdateCreditProfile_UpdateExistingProfile_Success() throws ExecutionException, InterruptedException {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(creditProfileRepository.findByCustomer(customer)).thenReturn(Optional.of(creditProfile));
        when(creditProfileRepository.save(any(CreditProfile.class))).thenReturn(creditProfile);

        CompletableFuture<CreditProfile> future = creditProfileService.createOrUpdateCreditProfile(createRequest);
        CreditProfile result = future.get();

        assertNotNull(result);
        assertEquals(createRequest.getCreditScore(), result.getCreditScore());
        assertEquals(createRequest.getMaxLoanAmount(), result.getMaxLoanAmount());
        verify(creditProfileRepository).save(creditProfile);
    }

    @Test
    void createOrUpdateCreditProfile_CustomerNotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            creditProfileService.createOrUpdateCreditProfile(createRequest).get();
        });
    }

    @Test
    void getCreditProfileByCustomerId_Found_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(creditProfileRepository.findByCustomer(customer)).thenReturn(Optional.of(creditProfile));

        Optional<CreditProfile> result = creditProfileService.getCreditProfileByCustomerId(1L);

        assertTrue(result.isPresent());
        assertEquals(creditProfile, result.get());
    }

    @Test
    void getCreditProfileByCustomerId_CustomerNotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> creditProfileService.getCreditProfileByCustomerId(1L));
    }

    @Test
    void getCreditProfileById_Found_Success() {
        when(creditProfileRepository.findById(1L)).thenReturn(Optional.of(creditProfile));
        Optional<CreditProfile> result = creditProfileService.getCreditProfileById(1L);
        assertTrue(result.isPresent());
        assertEquals(creditProfile, result.get());
    }

    @Test
    void getCreditProfileById_NotFound_ReturnsEmpty() {
        when(creditProfileRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<CreditProfile> result = creditProfileService.getCreditProfileById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void assessCreditWorthiness_Success() throws ExecutionException, InterruptedException {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(creditProfileRepository.findByCustomer(customer)).thenReturn(Optional.of(creditProfile));
        when(creditProfileRepository.save(any(CreditProfile.class))).thenReturn(creditProfile);

        CompletableFuture<CreditProfile> future = creditProfileService.assessCreditWorthiness(1L);
        CreditProfile result = future.get();

        assertNotNull(result);
        assertEquals(710, result.getCreditScore());
        assertEquals(new BigDecimal("11000.00"), result.getMaxLoanAmount());
        verify(creditProfileRepository).save(creditProfile);
    }

    @Test
    void assessCreditWorthiness_ProfileNotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(creditProfileRepository.findByCustomer(customer)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            creditProfileService.assessCreditWorthiness(1L).get();
        });
    }

    @Test
    void updateCreditScore_Success() throws ExecutionException, InterruptedException {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(creditProfileRepository.findByCustomer(customer)).thenReturn(Optional.of(creditProfile));
        when(creditProfileRepository.save(any(CreditProfile.class))).thenReturn(creditProfile);

        UpdateCreditScoreRequest request = new UpdateCreditScoreRequest();
        request.setNewScore(780);

        CompletableFuture<CreditProfile> future = creditProfileService.updateCreditScore(1L, request);
        CreditProfile result = future.get();

        assertEquals(780, result.getCreditScore());
        verify(creditProfileRepository).save(creditProfile);
    }

    @Test
    void updateMaxLoanAmount_Success() throws ExecutionException, InterruptedException {
        BigDecimal newMaxLoan = new BigDecimal("15000");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(creditProfileRepository.findByCustomer(customer)).thenReturn(Optional.of(creditProfile));
        when(creditProfileRepository.save(any(CreditProfile.class))).thenReturn(creditProfile);

        UpdateMaxLoanAmountRequest request = new UpdateMaxLoanAmountRequest();
        request.setNewMaxAmount(newMaxLoan);
        CompletableFuture<CreditProfile> future = creditProfileService.updateMaxLoanAmount(1L, request);
        CreditProfile result = future.get();

        assertEquals(newMaxLoan, result.getMaxLoanAmount());
        verify(creditProfileRepository).save(creditProfile);
    }

    @Test
    void updateCurrentDebt_Success() throws ExecutionException, InterruptedException {
        BigDecimal newDebt = new BigDecimal("500");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(creditProfileRepository.findByCustomer(customer)).thenReturn(Optional.of(creditProfile));
        when(creditProfileRepository.save(any(CreditProfile.class))).thenReturn(creditProfile);

        UpdateCurrentDebtRequest request = new UpdateCurrentDebtRequest();
        request.setNewDebtAmount(newDebt);
        CompletableFuture<CreditProfile> future = creditProfileService.updateCurrentDebt(1L, request);
        CreditProfile result = future.get();

        assertEquals(newDebt, result.getCurrentDebt());
        verify(creditProfileRepository).save(creditProfile);
    }
}