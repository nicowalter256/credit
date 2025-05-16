package com.metropol.credit.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
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

import com.metropol.credit.controllers.LoanApplicationController;
import com.metropol.credit.dtos.LoanApplicationDTO;
import com.metropol.credit.models.Message;
import com.metropol.credit.interfaces.LoanApplicationService;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.enums.LoanApplicationStatus;
import com.metropol.credit.models.requests.LoanApplicationRequest;

@ExtendWith(MockitoExtension.class)
public class LoanApplicationControllerUnitTest {

        @Mock
        private LoanApplicationService loanApplicationService;

        @Mock
        private ModelMapper modelMapper;

        @InjectMocks
        private LoanApplicationController loanApplicationController;

        private LoanApplication mockLoanApplication;
        private LoanApplicationDTO mockLoanApplicationDTO;
        private LoanApplicationRequest applicationRequest;

        @BeforeEach
        void setUp() {
                mockLoanApplication = new LoanApplication();
                mockLoanApplication.setId(1L);
                mockLoanApplication.setAmountRequested(new BigDecimal("1000.00"));
                mockLoanApplication.setStatus(LoanApplicationStatus.PENDING);

                mockLoanApplicationDTO = new LoanApplicationDTO();
                mockLoanApplicationDTO.setId(1L);
                mockLoanApplicationDTO.setAmountRequested(new BigDecimal("1000.00"));
                mockLoanApplicationDTO.setStatus(LoanApplicationStatus.PENDING);

                applicationRequest = new LoanApplicationRequest();
                applicationRequest.setCustomerId(1L);
                applicationRequest.setAmountRequested(new BigDecimal("1000.00"));
                applicationRequest.setPurpose("Test Purpose");

                when(modelMapper.map(any(LoanApplication.class), eq(LoanApplicationDTO.class)))
                                .thenReturn(mockLoanApplicationDTO);
        }

        @Test
        void submitLoanApplication_ShouldReturnAccepted() throws Exception {
                when(loanApplicationService.submitLoanApplication(any(LoanApplicationRequest.class)))
                                .thenReturn(CompletableFuture.completedFuture(mockLoanApplication));

                ResponseEntity<Message> response = loanApplicationController
                                .submitLoanApplication(applicationRequest);

                assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("Loan application submission for customer ID " + applicationRequest.getCustomerId()
                                + " accepted and is being processed.", response.getBody().getMessage());
                verify(loanApplicationService).submitLoanApplication(applicationRequest);
        }

        @Test
        void getLoanApplicationById_WhenFound_ShouldReturnOk() {
                when(loanApplicationService.findLoanApplicationById(1L)).thenReturn(Optional.of(mockLoanApplication));

                ResponseEntity<LoanApplicationDTO> response = loanApplicationController.getLoanApplicationById(1L);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(mockLoanApplicationDTO, response.getBody());
                verify(loanApplicationService).findLoanApplicationById(1L);
        }

        @Test
        void getLoanApplicationById_WhenNotFound_ShouldReturnNotFound() {
                when(loanApplicationService.findLoanApplicationById(99L)).thenReturn(Optional.empty());

                ResponseEntity<LoanApplicationDTO> response = loanApplicationController.getLoanApplicationById(99L);

                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                verify(loanApplicationService).findLoanApplicationById(99L);
        }

        @Test
        void getLoanApplicationsByCustomerId_ShouldReturnOk() {
                when(loanApplicationService.getLoanApplicationsByCustomerId(1L))
                                .thenReturn(Collections.singletonList(mockLoanApplication));

                ResponseEntity<List<LoanApplicationDTO>> response = loanApplicationController
                                .getLoanApplicationsByCustomerId(1L);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals(1, response.getBody().size());
                verify(loanApplicationService).getLoanApplicationsByCustomerId(1L);
        }

        @Test
        void getLoanApplicationsByStatus_ShouldReturnOk() {
                when(loanApplicationService.getLoanApplicationsByStatus(LoanApplicationStatus.PENDING))
                                .thenReturn(Collections.singletonList(mockLoanApplication));

                ResponseEntity<List<LoanApplicationDTO>> response = loanApplicationController
                                .getLoanApplicationsByStatus(LoanApplicationStatus.PENDING);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals(1, response.getBody().size());
                verify(loanApplicationService).getLoanApplicationsByStatus(LoanApplicationStatus.PENDING);
        }

        @Test
        void approveLoanApplication_ShouldReturnAccepted() throws Exception {
                when(loanApplicationService.approveLoanApplication(anyLong()))
                                .thenReturn(CompletableFuture.completedFuture(mockLoanApplication));

                ResponseEntity<Message> response = loanApplicationController
                                .approveLoanApplication(1L);

                assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("Loan application approval request for application ID 1 accepted and is being processed.",
                                response.getBody().getMessage());
                verify(loanApplicationService).approveLoanApplication(1L);
        }

        @Test
        void rejectLoanApplication_WithReason_ShouldReturnAccepted() throws Exception {
                String reason = "Insufficient funds";
                mockLoanApplication.setStatus(LoanApplicationStatus.REJECTED);
                mockLoanApplication.setRejectionReason(reason);

                when(loanApplicationService.rejectLoanApplication(anyLong(), anyString()))
                                .thenReturn(CompletableFuture.completedFuture(mockLoanApplication));

                ResponseEntity<Message> response = loanApplicationController
                                .rejectLoanApplication(1L, reason);

                assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("Loan application rejection request for application ID 1 accepted and is being processed.",
                                response.getBody().getMessage());
                verify(loanApplicationService).rejectLoanApplication(1L, reason);
        }

        @Test
        void rejectLoanApplication_WithoutReason_ShouldReturnAccepted() throws Exception {
                mockLoanApplication.setStatus(LoanApplicationStatus.REJECTED);

                when(loanApplicationService.rejectLoanApplication(anyLong(), eq(null)))
                                .thenReturn(CompletableFuture.completedFuture(mockLoanApplication));

                ResponseEntity<Message> response = loanApplicationController
                                .rejectLoanApplication(1L, null);

                assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("Loan application rejection request for application ID 1 accepted and is being processed.",
                                response.getBody().getMessage());
                verify(loanApplicationService).rejectLoanApplication(1L, null);
        }

        @Test
        void cancelLoanApplication_ShouldReturnAccepted() throws Exception {
                mockLoanApplication.setStatus(LoanApplicationStatus.CANCELED);
                when(loanApplicationService.cancelLoanApplication(anyLong()))
                                .thenReturn(CompletableFuture.completedFuture(mockLoanApplication));

                ResponseEntity<Message> response = loanApplicationController.cancelLoanApplication(1L);

                assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("Loan application cancellation request for application ID 1 accepted and is being processed.",
                                response.getBody().getMessage());
                verify(loanApplicationService).cancelLoanApplication(1L);
        }
}