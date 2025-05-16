package com.metropol.credit.unitTests; // Updated package

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.metropol.credit.controllers.CreditProfileController;
import com.metropol.credit.dtos.CreditProfileDTO;
import com.metropol.credit.interfaces.CreditProfileService;
import com.metropol.credit.models.Message;
import com.metropol.credit.models.entities.CreditProfile;
import com.metropol.credit.models.requests.UpdateCreditScoreRequest;
import com.metropol.credit.models.requests.UpdateCurrentDebtRequest;
import com.metropol.credit.models.requests.UpdateMaxLoanAmountRequest;
import com.metropol.credit.models.requests.CreateOrUpdateCreditProfileRequest;

@ExtendWith(MockitoExtension.class)
public class CreditProfileControllerUnitTest {

    @Mock
    private CreditProfileService creditProfileService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CreditProfileController creditProfileController;

    private CreditProfile mockCreditProfile;
    private CreditProfileDTO mockCreditProfileDTO;

    @BeforeEach
    void setUp() {
        mockCreditProfile = new CreditProfile();
        mockCreditProfile.setId(1L);
        mockCreditProfile.setCreditScore(750);
        mockCreditProfile.setMaxLoanAmount(new BigDecimal("20000.00"));
        mockCreditProfile.setCurrentDebt(new BigDecimal("5000.00"));
        mockCreditProfileDTO = new CreditProfileDTO();
        mockCreditProfileDTO.setId(1L);
        mockCreditProfileDTO.setCreditScore(750);
        mockCreditProfileDTO.setMaxLoanAmount(new BigDecimal("20000.00"));
        mockCreditProfileDTO.setCurrentDebt(new BigDecimal("5000.00"));
        when(modelMapper.map(any(CreditProfile.class), eq(CreditProfileDTO.class)))
                .thenReturn(mockCreditProfileDTO);
    }

    @Test
    void createOrUpdateCreditProfile_ShouldReturnAccepted() throws Exception {
        CreateOrUpdateCreditProfileRequest request = new CreateOrUpdateCreditProfileRequest();
        request.setCustomerId(1L);
        request.setCreditScore(700);
        request.setMaxLoanAmount(new BigDecimal("15000.00"));
        request.setCurrentDebt(new BigDecimal("2500.00"));

        when(creditProfileService.createOrUpdateCreditProfile(request))
                .thenReturn(CompletableFuture.completedFuture(mockCreditProfile));

        ResponseEntity<Message> response = creditProfileController
                .createOrUpdateCreditProfile(request);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Credit profile creation/update request for customer ID " + request.getCustomerId()
                + " accepted and is being processed.", response.getBody().getMessage());

        verify(creditProfileService).createOrUpdateCreditProfile(request);

    }

    @Test
    void getCreditProfileByCustomerId_WhenFound_ShouldReturnOk() {
        Long customerId = 1L;
        when(creditProfileService.getCreditProfileByCustomerId(customerId))
                .thenReturn(Optional.of(mockCreditProfile));

        ResponseEntity<CreditProfileDTO> response = creditProfileController.getCreditProfileByCustomerId(customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockCreditProfileDTO, response.getBody());

        verify(creditProfileService).getCreditProfileByCustomerId(customerId);
        verify(modelMapper).map(mockCreditProfile, CreditProfileDTO.class);
    }

    @Test
    void getCreditProfileByCustomerId_WhenNotFound_ShouldReturnNotFound() {
        Long customerId = 99L;
        when(creditProfileService.getCreditProfileByCustomerId(customerId))
                .thenReturn(Optional.empty());
        ResponseEntity<CreditProfileDTO> response = creditProfileController.getCreditProfileByCustomerId(customerId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(creditProfileService).getCreditProfileByCustomerId(customerId);
    }

    @Test
    void assessCreditWorthiness_ShouldReturnAccepted() {
        Long customerId = 1L;
        when(creditProfileService.assessCreditWorthiness(customerId))
                .thenReturn(CompletableFuture.completedFuture(mockCreditProfile));

        ResponseEntity<Message> response = creditProfileController.assessCreditWorthiness(customerId);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Credit worthiness assessment request for customer ID " + customerId
                + " accepted and is being processed.", response.getBody().getMessage());
        verify(creditProfileService).assessCreditWorthiness(customerId);
    }

    @Test
    void updateCreditScore_ShouldReturnAccepted() {
        Long customerId = 1L;
        UpdateCreditScoreRequest request = new UpdateCreditScoreRequest();
        request.setNewScore(780);

        when(creditProfileService.updateCreditScore(eq(customerId), any(UpdateCreditScoreRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockCreditProfile));

        ResponseEntity<Message> response = creditProfileController.updateCreditScore(customerId, request);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Credit score update request for customer ID " + customerId
                + " accepted and is being processed.", response.getBody().getMessage());
        verify(creditProfileService).updateCreditScore(customerId, request);
    }

    @Test
    void updateMaxLoanAmount_ShouldReturnAccepted() {
        Long customerId = 1L;
        UpdateMaxLoanAmountRequest request = new UpdateMaxLoanAmountRequest();
        request.setNewMaxAmount(new BigDecimal("25000.00"));

        when(creditProfileService.updateMaxLoanAmount(eq(customerId), any(UpdateMaxLoanAmountRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockCreditProfile));

        ResponseEntity<Message> response = creditProfileController.updateMaxLoanAmount(customerId, request);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Maximum loan amount update request for customer ID " + customerId
                + " accepted and is being processed.", response.getBody().getMessage());
        verify(creditProfileService).updateMaxLoanAmount(customerId, request);
    }

    @Test
    void updateCurrentDebt_ShouldReturnAccepted() {
        Long customerId = 1L;
        UpdateCurrentDebtRequest request = new UpdateCurrentDebtRequest();
        request.setNewDebtAmount(new BigDecimal("3000.00"));

        when(creditProfileService.updateCurrentDebt(eq(customerId), any(UpdateCurrentDebtRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockCreditProfile));

        ResponseEntity<Message> response = creditProfileController.updateCurrentDebt(customerId, request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Current debt update request for customer ID " + customerId + " accepted and is being processed.",
                response.getBody().getMessage());
        verify(creditProfileService).updateCurrentDebt(customerId, request);
    }
}