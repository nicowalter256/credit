package com.metropol.credit.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import com.metropol.credit.controllers.LoanController;
import com.metropol.credit.dtos.LoanDTO;
import com.metropol.credit.models.Message;
import com.metropol.credit.interfaces.LoanService;
import com.metropol.credit.models.entities.Loan;
import com.metropol.credit.models.enums.LoanStatus;
import com.metropol.credit.models.requests.UpdateLoanBalanceRequest;
import com.metropol.credit.models.requests.UpdateLoanStatusRequest;

@ExtendWith(MockitoExtension.class)
public class LoanControllerUnitTest {

    @Mock
    private LoanService loanService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LoanController loanController;

    private Loan mockLoan;
    private LoanDTO mockLoanDTO;
    private UpdateLoanStatusRequest updateLoanStatusRequest;
    private UpdateLoanBalanceRequest updateLoanBalanceRequest;

    @BeforeEach
    void setUp() {
        mockLoan = new Loan();
        mockLoan.setId(1L);
        mockLoan.setAmountApproved(new BigDecimal("5000.00"));
        mockLoan.setStatus(LoanStatus.ACTIVE);
        mockLoan.setOutstandingBalance(new BigDecimal("4500.00"));

        mockLoanDTO = new LoanDTO();
        mockLoanDTO.setId(1L);
        mockLoanDTO.setAmountApproved(new BigDecimal("5000.00"));
        mockLoanDTO.setStatus(LoanStatus.ACTIVE.name());
        mockLoanDTO.setOutstandingBalance(new BigDecimal("4500.00"));

        updateLoanStatusRequest = new UpdateLoanStatusRequest();
        updateLoanStatusRequest.setNewStatus(LoanStatus.PAID_OFF);

        updateLoanBalanceRequest = new UpdateLoanBalanceRequest();
        updateLoanBalanceRequest.setNewBalance(new BigDecimal("4000.00"));

        when(modelMapper.map(any(Loan.class), eq(LoanDTO.class))).thenReturn(mockLoanDTO);
    }

    @Test
    void getLoanById_WhenFound_ShouldReturnOk() {
        when(loanService.findLoanById(1L)).thenReturn(Optional.of(mockLoan));

        ResponseEntity<LoanDTO> response = loanController.getLoanById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockLoanDTO, response.getBody());
        verify(loanService).findLoanById(1L);
    }

    @Test
    void getLoanById_WhenNotFound_ShouldReturnNotFound() {
        when(loanService.findLoanById(99L)).thenReturn(Optional.empty());

        ResponseEntity<LoanDTO> response = loanController.getLoanById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(loanService).findLoanById(99L);
    }

    @Test
    void getLoansByCustomerId_ShouldReturnOk() {
        when(loanService.getLoansByCustomerId(1L)).thenReturn(Collections.singletonList(mockLoan));

        ResponseEntity<List<LoanDTO>> response = loanController.getLoansByCustomerId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(loanService).getLoansByCustomerId(1L);
    }

    @Test
    void getLoansByStatus_ShouldReturnOk() {
        when(loanService.getLoansByStatus(LoanStatus.ACTIVE)).thenReturn(Collections.singletonList(mockLoan));

        ResponseEntity<List<LoanDTO>> response = loanController.getLoansByStatus(LoanStatus.ACTIVE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(loanService).getLoansByStatus(LoanStatus.ACTIVE);
    }

    @Test
    void updateLoanStatus_ShouldReturnAccepted() throws Exception {
        when(loanService.updateLoanStatus(anyLong(), any(UpdateLoanStatusRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockLoan));

        ResponseEntity<Message> response = loanController.updateLoanStatus(1L,
                updateLoanStatusRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Loan status update request for loan ID 1 accepted and is being processed.",
                response.getBody().getMessage());

        verify(loanService).updateLoanStatus(1L, updateLoanStatusRequest);

    }

    @Test
    void updateOutstandingBalance_ShouldReturnOk() {
        Long loanId = 1L;
        mockLoan.setOutstandingBalance(updateLoanBalanceRequest.getNewBalance()); // Simulate update
        mockLoanDTO.setOutstandingBalance(updateLoanBalanceRequest.getNewBalance());

        when(loanService.updateOutstandingBalance(eq(loanId), eq(updateLoanBalanceRequest.getNewBalance())))
                .thenReturn(mockLoan);

        ResponseEntity<LoanDTO> response = loanController.updateOutstandingBalance(loanId, updateLoanBalanceRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updateLoanBalanceRequest.getNewBalance(), response.getBody().getOutstandingBalance());
        verify(loanService).updateOutstandingBalance(loanId, updateLoanBalanceRequest.getNewBalance());
    }

    @Test
    void updateOutstandingBalance_WhenLoanNotFound_ShouldReturnNotFound() {
        Long loanId = 99L;
        when(loanService.updateOutstandingBalance(eq(loanId), any(BigDecimal.class))).thenReturn(null);

        ResponseEntity<LoanDTO> response = loanController.updateOutstandingBalance(loanId, updateLoanBalanceRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(loanService).updateOutstandingBalance(loanId, updateLoanBalanceRequest.getNewBalance());
    }
}