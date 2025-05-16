package com.metropol.credit.controllers;

import java.util.concurrent.CompletableFuture;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metropol.credit.dtos.CreditProfileDTO;
import com.metropol.credit.interfaces.CreditProfileService;
import com.metropol.credit.models.Message;
import com.metropol.credit.models.entities.CreditProfile;
import com.metropol.credit.models.requests.CreateOrUpdateCreditProfileRequest;
import com.metropol.credit.models.requests.UpdateCreditScoreRequest;
import com.metropol.credit.models.requests.UpdateCurrentDebtRequest;
import com.metropol.credit.models.requests.UpdateMaxLoanAmountRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/credit-profiles")
@Tag(name = "Credit Profile Management", description = "APIs for managing customer credit profiles")
public class CreditProfileController {

        @Autowired
        private CreditProfileService creditProfileService;
        @Autowired
        private ModelMapper modelMapper;

        @PostMapping
        @Operation(summary = "Create or update a credit profile", description = "Creates a new credit profile for a customer or updates an existing one.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Credit profile creation/update request accepted and is being processed."),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "404", description = "Associated customer not found")
        })
        public ResponseEntity<Message> createOrUpdateCreditProfile(
                        @Valid @RequestBody CreateOrUpdateCreditProfileRequest request) {
                CompletableFuture<CreditProfile> profileFuture = creditProfileService
                                .createOrUpdateCreditProfile(request);

                profileFuture.exceptionally(ex -> {
                        System.err.println("Async credit profile creation/update failed for customer ID "
                                        + request.getCustomerId() + ": " + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("Credit profile creation/update request for customer ID "
                                                + request.getCustomerId() + " accepted and is being processed."));

        }

        @GetMapping("/customer/{customerId}")
        @Operation(summary = "Get credit profile by customer ID", description = "Retrieves the credit profile for a specific customer.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Credit profile found"),
                        @ApiResponse(responseCode = "404", description = "Customer or credit profile not found")
        })
        public ResponseEntity<CreditProfileDTO> getCreditProfileByCustomerId(
                        @Parameter(description = "ID of the customer whose credit profile to retrieve") @PathVariable Long customerId) {
                return creditProfileService.getCreditProfileByCustomerId(customerId)
                                .map(profile -> ResponseEntity.ok(modelMapper.map(profile, CreditProfileDTO.class)))
                                .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping("/{profileId}")
        @Operation(summary = "Get credit profile by profile ID", description = "Retrieves a specific credit profile by its unique ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Credit profile found"),
                        @ApiResponse(responseCode = "404", description = "Credit profile not found")
        })
        public ResponseEntity<CreditProfileDTO> getCreditProfileById(
                        @Parameter(description = "ID of the credit profile to retrieve") @PathVariable Long profileId) {
                return creditProfileService.getCreditProfileById(profileId)
                                .map(profile -> ResponseEntity.ok(modelMapper.map(profile, CreditProfileDTO.class)))
                                .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping("/customer/{customerId}/assess")
        @Operation(summary = "Assess credit worthiness for a customer", description = "Performs a credit worthiness assessment for a customer and returns their updated credit profile.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Credit worthiness assessment request accepted and is being processed."),
                        @ApiResponse(responseCode = "404", description = "Customer not found")
        })
        public ResponseEntity<Message> assessCreditWorthiness(
                        @Parameter(description = "ID of the customer to assess") @PathVariable Long customerId) {
                CompletableFuture<CreditProfile> assessmentFuture = creditProfileService
                                .assessCreditWorthiness(customerId);

                assessmentFuture.exceptionally(ex -> {
                        System.err.println("Async credit worthiness assessment failed for customer ID " + customerId
                                        + ": " + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("Credit worthiness assessment request for customer ID " + customerId
                                                + " accepted and is being processed."));
        }

        @PutMapping("/customer/{customerId}/score")
        @Operation(summary = "Update credit score for a customer", description = "Updates the credit score for a specific customer's profile.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Credit score update request accepted and is being processed."),
                        @ApiResponse(responseCode = "404", description = "Customer or credit profile not found")
        })
        public ResponseEntity<Message> updateCreditScore(
                        @Parameter(description = "ID of the customer whose credit score to update") @PathVariable Long customerId,
                        @Parameter(description = "Request body containing the new credit score") @Valid @RequestBody UpdateCreditScoreRequest request) {
                CompletableFuture<CreditProfile> scoreUpdateFuture = creditProfileService
                                .updateCreditScore(customerId, request);

                scoreUpdateFuture.exceptionally(ex -> {
                        System.err.println("Async credit score update failed for customer ID " + customerId + ": "
                                        + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("Credit score update request for customer ID " + customerId
                                                + " accepted and is being processed."));
        }

        @PutMapping("/customer/{customerId}/max-loan")
        @Operation(summary = "Update maximum loan amount for a customer", description = "Updates the maximum loan amount for a specific customer's profile.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Maximum loan amount update request accepted and is being processed."),
                        @ApiResponse(responseCode = "404", description = "Customer or credit profile not found")
        })
        public ResponseEntity<Message> updateMaxLoanAmount(
                        @Parameter(description = "ID of the customer whose max loan amount to update") @PathVariable Long customerId,
                        @Parameter(description = "Request body containing the new maximum loan amount") @Valid @RequestBody UpdateMaxLoanAmountRequest request) {
                CompletableFuture<CreditProfile> maxLoanUpdateFuture = creditProfileService
                                .updateMaxLoanAmount(customerId, request);

                maxLoanUpdateFuture.exceptionally(ex -> {
                        System.err.println("Async maximum loan amount update failed for customer ID " + customerId
                                        + ": " + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("Maximum loan amount update request for customer ID " + customerId
                                                + " accepted and is being processed."));
        }

        @PutMapping("/customer/{customerId}/debt")
        @Operation(summary = "Update current debt for a customer", description = "Updates the current debt amount for a specific customer's profile.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Current debt update request accepted and is being processed."),
                        @ApiResponse(responseCode = "404", description = "Customer or credit profile not found")
        })
        public ResponseEntity<Message> updateCurrentDebt(
                        @Parameter(description = "ID of the customer whose current debt to update") @PathVariable Long customerId,
                        @Parameter(description = "Request body containing the new current debt amount") @Valid @RequestBody UpdateCurrentDebtRequest request) {
                CompletableFuture<CreditProfile> debtUpdateFuture = creditProfileService
                                .updateCurrentDebt(customerId, request);

                debtUpdateFuture.exceptionally(ex -> {
                        System.err.println("Async current debt update failed for customer ID " + customerId + ": "
                                        + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("Current debt update request for customer ID " + customerId
                                                + " accepted and is being processed."));
        }
}