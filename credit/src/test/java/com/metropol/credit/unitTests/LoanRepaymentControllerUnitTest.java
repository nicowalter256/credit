package com.metropol.credit.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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

import com.metropol.credit.controllers.LoanRepaymentController;
import com.metropol.credit.dtos.LoanRepaymentDTO;
import com.metropol.credit.models.Message;
import com.metropol.credit.interfaces.LoanRepaymentService;
import com.metropol.credit.models.entities.LoanRepayment;
import com.metropol.credit.models.requests.RecordRepaymentRequest;

@ExtendWith(MockitoExtension.class)
public class LoanRepaymentControllerUnitTest {

    @Mock
    private LoanRepaymentService loanRepaymentService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LoanRepaymentController loanRepaymentController;

    private LoanRepayment mockLoanRepayment;
    private LoanRepaymentDTO mockLoanRepaymentDTO;
    private RecordRepaymentRequest recordRepaymentRequest;

    @BeforeEach
    void setUp() {
        mockLoanRepayment = new LoanRepayment();
        mockLoanRepayment.setId(1L);
        mockLoanRepayment.setAmountPaid(new BigDecimal("100.00"));
        mockLoanRepayment.setPaymentDate(ZonedDateTime.now());

        mockLoanRepaymentDTO = new LoanRepaymentDTO();
        mockLoanRepaymentDTO.setId(1L);
        mockLoanRepaymentDTO.setAmountPaid(new BigDecimal("100.00"));
        mockLoanRepaymentDTO.setPaymentDate(ZonedDateTime.now());

        recordRepaymentRequest = new RecordRepaymentRequest();
        recordRepaymentRequest.setLoanId(1L);
        recordRepaymentRequest.setAmountPaid(new BigDecimal("100.00"));
        recordRepaymentRequest.setPaymentMethod("CASH");

        when(modelMapper.map(any(LoanRepayment.class), eq(LoanRepaymentDTO.class))).thenReturn(mockLoanRepaymentDTO);
    }

    @Test
    void recordRepayment_ShouldReturnAccepted() throws Exception {
        when(loanRepaymentService.recordRepayment(any(RecordRepaymentRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockLoanRepayment));

        ResponseEntity<Message> response = loanRepaymentController
                .recordRepayment(recordRepaymentRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Loan repayment recording request for loan ID " + recordRepaymentRequest.getLoanId()
                + " accepted and is being processed.", response.getBody().getMessage());

        verify(loanRepaymentService).recordRepayment(recordRepaymentRequest);

    }

    @Test
    void getRepaymentById_WhenFound_ShouldReturnOk() {
        when(loanRepaymentService.findRepaymentById(1L)).thenReturn(Optional.of(mockLoanRepayment));

        ResponseEntity<LoanRepaymentDTO> response = loanRepaymentController.getRepaymentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockLoanRepaymentDTO, response.getBody());
        verify(loanRepaymentService).findRepaymentById(1L);
    }

    @Test
    void getRepaymentById_WhenNotFound_ShouldReturnNotFound() {
        when(loanRepaymentService.findRepaymentById(99L)).thenReturn(Optional.empty());

        ResponseEntity<LoanRepaymentDTO> response = loanRepaymentController.getRepaymentById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(loanRepaymentService).findRepaymentById(99L);
    }

    @Test
    void getRepaymentsForLoan_ShouldReturnOk() {
        when(loanRepaymentService.getRepaymentsForLoan(1L)).thenReturn(Collections.singletonList(mockLoanRepayment));

        ResponseEntity<List<LoanRepaymentDTO>> response = loanRepaymentController.getRepaymentsForLoan(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(loanRepaymentService).getRepaymentsForLoan(1L);
    }

    @Test
    void getRepaymentsByDateRange_ShouldReturnOk() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        when(loanRepaymentService.getRepaymentsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(mockLoanRepayment));

        ResponseEntity<List<LoanRepaymentDTO>> response = loanRepaymentController.getRepaymentsByDateRange(start, end);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(loanRepaymentService).getRepaymentsByDateRange(start, end);
    }
}