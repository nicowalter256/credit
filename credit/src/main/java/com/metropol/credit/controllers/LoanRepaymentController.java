package com.metropol.credit.controllers;

import com.metropol.credit.models.Message;
import com.metropol.credit.dtos.LoanRepaymentDTO;
import com.metropol.credit.interfaces.LoanRepaymentService;
import com.metropol.credit.models.entities.LoanRepayment;
import com.metropol.credit.models.requests.RecordRepaymentRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/repayments")
@Tag(name = "Loan Repayment Management", description = "APIs for managing loan repayments")
public class LoanRepaymentController {

        @Autowired
        private LoanRepaymentService loanRepaymentService;
        @Autowired
        private ModelMapper modelMapper;

        @PostMapping
        @Operation(summary = "Record a new loan repayment", description = "Records a payment made towards an existing loan.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Repayment recording request accepted and is being processed."),
                        @ApiResponse(responseCode = "400", description = "Invalid repayment data"),
                        @ApiResponse(responseCode = "404", description = "Associated loan not found")
        })
        public ResponseEntity<Message> recordRepayment(
                        @Valid @RequestBody RecordRepaymentRequest repaymentRequest) {
                CompletableFuture<LoanRepayment> repaymentFuture = loanRepaymentService
                                .recordRepayment(repaymentRequest);

                repaymentFuture.exceptionally(ex -> {
                        System.err.println("Async loan repayment recording failed for loan ID "
                                        + repaymentRequest.getLoanId() + ": " + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("Loan repayment recording request for loan ID "
                                                + repaymentRequest.getLoanId() + " accepted and is being processed."));

        }

        @GetMapping("/{repaymentId}")
        @Operation(summary = "Get repayment by ID", description = "Retrieves a specific loan repayment by its unique ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Repayment found"),
                        @ApiResponse(responseCode = "404", description = "Repayment not found")
        })
        public ResponseEntity<LoanRepaymentDTO> getRepaymentById(
                        @Parameter(description = "ID of the repayment to retrieve") @PathVariable Long repaymentId) {
                return loanRepaymentService.findRepaymentById(repaymentId)
                                .map(repayment -> ResponseEntity.ok(modelMapper.map(repayment, LoanRepaymentDTO.class)))
                                .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping("/loan/{loanId}")
        @Operation(summary = "Get repayments for a loan", description = "Retrieves all repayments made for a specific loan.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved repayments"),
                        @ApiResponse(responseCode = "404", description = "Loan not found or no repayments")
        })
        public ResponseEntity<List<LoanRepaymentDTO>> getRepaymentsForLoan(
                        @Parameter(description = "ID of the loan whose repayments to retrieve") @PathVariable Long loanId) {
                List<LoanRepayment> repayments = loanRepaymentService.getRepaymentsForLoan(loanId);
                List<LoanRepaymentDTO> dtos = repayments.stream()
                                .map(repayment -> modelMapper.map(repayment, LoanRepaymentDTO.class))
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }

        @GetMapping("/date-range")
        @Operation(summary = "Get repayments by date range", description = "Retrieves all repayments made within a specified date range.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved repayments")
        public ResponseEntity<List<LoanRepaymentDTO>> getRepaymentsByDateRange(
                        @Parameter(description = "Start date and time for the range (ISO DATE_TIME format, e.g., 2023-01-01T00:00:00)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                        @Parameter(description = "End date and time for the range (ISO DATE_TIME format, e.g., 2023-01-31T23:59:59)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
                List<LoanRepayment> repayments = loanRepaymentService.getRepaymentsByDateRange(startDate, endDate);
                List<LoanRepaymentDTO> dtos = repayments.stream()
                                .map(repayment -> modelMapper.map(repayment, LoanRepaymentDTO.class))
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }
}