package com.metropol.credit.controllers;

import com.metropol.credit.models.Message;
import com.metropol.credit.dtos.LoanApplicationDTO;
import com.metropol.credit.interfaces.LoanApplicationService;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.enums.LoanApplicationStatus;
import com.metropol.credit.models.requests.LoanApplicationRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/loan-applications")
@Tag(name = "Loan Application Management", description = "APIs for managing loan applications")
public class LoanApplicationController {

        @Autowired
        private LoanApplicationService loanApplicationService;
        @Autowired
        private ModelMapper modelMapper;

        @PostMapping
        @Operation(summary = "Submit a new loan application", description = "Allows a customer to submit a new application for a loan.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Loan application submission request accepted and is being processed."),
                        @ApiResponse(responseCode = "400", description = "Invalid application data")
        })
        public ResponseEntity<Message> submitLoanApplication(
                        @Valid @RequestBody LoanApplicationRequest applicationRequest) {
                CompletableFuture<LoanApplication> submissionFuture = loanApplicationService
                                .submitLoanApplication(applicationRequest);

                submissionFuture.exceptionally(ex -> {
                        System.err.println("Async loan application submission failed for customer ID "
                                        + applicationRequest.getCustomerId() + ": " + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("Loan application submission for customer ID "
                                                + applicationRequest.getCustomerId()
                                                + " accepted and is being processed."));

        }

        @GetMapping("/{applicationId}")
        @Operation(summary = "Get loan application by ID", description = "Retrieves a specific loan application by its unique ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Loan application found"),
                        @ApiResponse(responseCode = "404", description = "Loan application not found")
        })
        public ResponseEntity<LoanApplicationDTO> getLoanApplicationById(
                        @Parameter(description = "ID of the loan application to retrieve") @PathVariable Long applicationId) {
                return loanApplicationService.findLoanApplicationById(applicationId)
                                .map(application -> ResponseEntity
                                                .ok(modelMapper.map(application, LoanApplicationDTO.class)))
                                .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping("/customer/{customerId}")
        @Operation(summary = "Get loan applications by customer ID", description = "Retrieves all loan applications submitted by a specific customer.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved loan applications"),
                        @ApiResponse(responseCode = "404", description = "Customer not found or no applications")
        })
        public ResponseEntity<List<LoanApplicationDTO>> getLoanApplicationsByCustomerId(
                        @Parameter(description = "ID of the customer whose applications to retrieve") @PathVariable Long customerId) {
                List<LoanApplication> applications = loanApplicationService.getLoanApplicationsByCustomerId(customerId);
                List<LoanApplicationDTO> dtos = applications.stream()
                                .map(app -> modelMapper.map(app, LoanApplicationDTO.class))
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }

        @GetMapping("/status/{status}")
        @Operation(summary = "Get loan applications by status", description = "Retrieves all loan applications that match a specific status.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved loan applications")
        public ResponseEntity<List<LoanApplicationDTO>> getLoanApplicationsByStatus(
                        @Parameter(description = "Status of the loan applications to retrieve (e.g., PENDING_REVIEW, APPROVED, REJECTED)") @PathVariable LoanApplicationStatus status) {
                List<LoanApplication> applications = loanApplicationService.getLoanApplicationsByStatus(status);
                List<LoanApplicationDTO> dtos = applications.stream()
                                .map(app -> modelMapper.map(app, LoanApplicationDTO.class))
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }

        @PostMapping("/{applicationId}/approve")
        @Operation(summary = "Approve a loan application", description = "Marks a loan application as approved.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Loan application approval request accepted and is being processed."),
                        @ApiResponse(responseCode = "404", description = "Loan application not found")
        })
        public ResponseEntity<Message> approveLoanApplication(
                        @Parameter(description = "ID of the loan application to approve") @PathVariable Long applicationId)
                        throws Exception {
                CompletableFuture<LoanApplication> approvalFuture = loanApplicationService
                                .approveLoanApplication(applicationId);

                approvalFuture.exceptionally(ex -> {
                        System.err.println("Async loan application approval failed for application ID " + applicationId
                                        + ": " + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("Loan application approval request for application ID "
                                                + applicationId + " accepted and is being processed."));
        }

        @PostMapping("/{applicationId}/reject")
        @Operation(summary = "Reject a loan application", description = "Marks a loan application as rejected, optionally with a reason.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Loan application rejection request accepted and is being processed."),
                        @ApiResponse(responseCode = "404", description = "Loan application not found")
        })
        public ResponseEntity<Message> rejectLoanApplication(
                        @Parameter(description = "ID of the loan application to reject") @PathVariable Long applicationId,
                        @Parameter(description = "Reason for rejection (optional)") @RequestBody(required = false) String reason) {
                CompletableFuture<LoanApplication> rejectionFuture = loanApplicationService
                                .rejectLoanApplication(applicationId, reason);

                rejectionFuture.exceptionally(ex -> {
                        System.err.println("Async loan application rejection failed for application ID " + applicationId
                                        + ": " + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("Loan application rejection request for application ID "
                                                + applicationId + " accepted and is being processed."));
        }

        @PostMapping("/{applicationId}/cancel")
        @Operation(summary = "Cancel a loan application", description = "Marks a loan application as cancelled by the user or system.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Loan application cancellation request accepted and is being processed."),
                        @ApiResponse(responseCode = "404", description = "Loan application not found")
        })
        public ResponseEntity<Message> cancelLoanApplication(
                        @Parameter(description = "ID of the loan application to cancel") @PathVariable Long applicationId) {
                CompletableFuture<LoanApplication> cancellationFuture = loanApplicationService
                                .cancelLoanApplication(applicationId);

                cancellationFuture.exceptionally(ex -> {
                        System.err.println("Async loan application cancellation failed for application ID "
                                        + applicationId + ": " + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("Loan application cancellation request for application ID "
                                                + applicationId + " accepted and is being processed."));
        }
}
