package com.metropol.credit.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metropol.credit.dtos.LoanDTO;
import com.metropol.credit.interfaces.LoanService;
import com.metropol.credit.models.Message;
import com.metropol.credit.models.entities.Loan;
import com.metropol.credit.models.enums.LoanStatus;
import com.metropol.credit.models.requests.UpdateLoanBalanceRequest;
import com.metropol.credit.models.requests.UpdateLoanStatusRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/loans")
@Tag(name = "Loan Management", description = "APIs for managing active loans")
public class LoanController {

        @Autowired
        private LoanService loanService;
        @Autowired
        private ModelMapper modelMapper;

        @GetMapping("/{loanId}")
        @Operation(summary = "Get loan by ID", description = "Retrieves a specific loan by its unique ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Loan found"),
                        @ApiResponse(responseCode = "404", description = "Loan not found")
        })
        public ResponseEntity<LoanDTO> getLoanById(
                        @Parameter(description = "ID of the loan to retrieve") @PathVariable Long loanId) {
                return loanService.findLoanById(loanId)
                                .map(loan -> ResponseEntity.ok(modelMapper.map(loan, LoanDTO.class)))
                                .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping("/customer/{customerId}")
        @Operation(summary = "Get loans by customer ID", description = "Retrieves all loans associated with a specific customer.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved loans"),
                        @ApiResponse(responseCode = "404", description = "Customer not found or no loans")
        })
        public ResponseEntity<List<LoanDTO>> getLoansByCustomerId(
                        @Parameter(description = "ID of the customer whose loans to retrieve") @PathVariable Long customerId) {
                List<Loan> loans = loanService.getLoansByCustomerId(customerId);
                List<LoanDTO> dtos = loans.stream()
                                .map(loan -> modelMapper.map(loan, LoanDTO.class))
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }

        @GetMapping("/status/{status}")
        @Operation(summary = "Get loans by status", description = "Retrieves all loans that match a specific status.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved loans")
        public ResponseEntity<List<LoanDTO>> getLoansByStatus(
                        @Parameter(description = "Status of the loans to retrieve (e.g., ACTIVE, PAID_OFF, DEFAULTED)") @PathVariable LoanStatus status) {
                List<Loan> loans = loanService.getLoansByStatus(status);
                List<LoanDTO> dtos = loans.stream()
                                .map(loan -> modelMapper.map(loan, LoanDTO.class))
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }

        @PutMapping("/{loanId}/status")
        @Operation(summary = "Update loan status", description = "Updates the status of an existing loan.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Loan status update request accepted and is being processed."),
                        @ApiResponse(responseCode = "400", description = "Invalid status data"),
                        @ApiResponse(responseCode = "404", description = "Loan not found")
        })
        public ResponseEntity<Message> updateLoanStatus(
                        @Parameter(description = "ID of the loan whose status to update") @PathVariable Long loanId,
                        @Valid @RequestBody UpdateLoanStatusRequest statusRequest) {
                CompletableFuture<Loan> updateFuture = loanService.updateLoanStatus(loanId, statusRequest);

                updateFuture.exceptionally(ex -> {
                        System.err.println("Async loan status update failed for loan ID " + loanId + ": "
                                        + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("Loan status update request for loan ID " + loanId
                                                + " accepted and is being processed."));
        }

        @PutMapping("/{loanId}/balance")
        @Operation(summary = "Update loan outstanding balance", description = "Manually updates the outstanding balance of a loan. Use with caution, typically repayments handle this.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Loan balance updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Loan not found")
        })
        public ResponseEntity<LoanDTO> updateOutstandingBalance(
                        @Parameter(description = "ID of the loan whose balance to update") @PathVariable Long loanId,
                        @Parameter(description = "Request body containing the new outstanding balance") @Valid @RequestBody UpdateLoanBalanceRequest request) {
                Loan updatedLoan = loanService.updateOutstandingBalance(loanId, request.getNewBalance());
                if (updatedLoan != null) {
                        return ResponseEntity.ok(modelMapper.map(updatedLoan, LoanDTO.class));
                }
                return ResponseEntity.notFound().build();
        }
}