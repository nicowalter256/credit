package com.metropol.credit.controllers;

import com.metropol.credit.dtos.CustomerDTO;
import com.metropol.credit.dtos.LoanApplicationDTO;
import com.metropol.credit.models.Message;
import com.metropol.credit.interfaces.CustomerService;
import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.requests.CustomerRegistrationRequest;
import com.metropol.credit.models.requests.UpdateCustomerDetailsRequest;
import com.metropol.credit.models.requests.UserLogin;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Management", description = "APIs for managing customer information")
public class CustomerController {

        @Autowired
        private CustomerService customerService;
        @Autowired
        private ModelMapper modelMapper;

        @PostMapping
        @Operation(summary = "Register a new customer", description = "Creates a new customer profile in the system.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Customer registration request accepted and is being processed."),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        public ResponseEntity<Message> registerCustomer(
                        @Valid @RequestBody CustomerRegistrationRequest registrationRequest) throws Exception {

                CompletableFuture<Customer> registrationFuture = customerService.registerCustomer(registrationRequest);

                registrationFuture.exceptionally(ex -> {
                        System.err.println("Async customer registration failed: " + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("Customer registration request accepted and is being processed."));
        }

        /**
         * 
         * <p>
         * Returns the following status codes:
         * <ul>
         * <li>403 - when user login credentails are wrong</li>
         * <li>400 - when one of the required fields is not provided</li>
         * 
         * </ul>
         */
        @PostMapping("/login")
        ResponseEntity<?> login(@RequestBody @Valid UserLogin userLogin) throws Exception {

                return ResponseEntity.ok(customerService.login(userLogin.getEmail(), userLogin.getPassword()));

        }

        @GetMapping("/{customerId}")
        @Operation(summary = "Get customer by ID", description = "Retrieves a specific customer by their unique ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Customer found"),
                        @ApiResponse(responseCode = "404", description = "Customer not found")
        })
        public ResponseEntity<CustomerDTO> getCustomerById(
                        @Parameter(description = "ID of the customer to be retrieved") @PathVariable Long customerId) {
                return customerService.findCustomerById(customerId)
                                .map(customer -> ResponseEntity.ok(modelMapper.map(customer, CustomerDTO.class)))
                                .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping("/email/{email}")
        @Operation(summary = "Get customer by email", description = "Retrieves a specific customer by their email address.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Customer found"),
                        @ApiResponse(responseCode = "404", description = "Customer not found")
        })
        public ResponseEntity<CustomerDTO> getCustomerByEmail(
                        @Parameter(description = "Email address of the customer to be retrieved") @PathVariable String email) {
                return customerService.findCustomerByEmail(email)
                                .map(customer -> ResponseEntity.ok(modelMapper.map(customer, CustomerDTO.class)))
                                .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping
        @Operation(summary = "Get all customers", description = "Retrieves a list of all registered customers.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of customers")
        public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
                List<Customer> customers = customerService.findAllCustomers();
                List<CustomerDTO> customerDTOs = customers.stream()
                                .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                                .collect(Collectors.toList());
                return ResponseEntity.ok(customerDTOs);
        }

        @PutMapping("/{customerId}")
        @Operation(summary = "Update customer details", description = "Updates the details of an existing customer.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Customer details updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "404", description = "Customer not found")
        })
        public ResponseEntity<CustomerDTO> updateCustomerDetails(
                        @Parameter(description = "ID of the customer to be updated") @PathVariable Long customerId,
                        @Valid @RequestBody UpdateCustomerDetailsRequest updateRequest) {
                Customer updatedCustomer = customerService.updateCustomerDetails(customerId, updateRequest);
                if (updatedCustomer != null) {
                        return ResponseEntity.ok(modelMapper.map(updatedCustomer, CustomerDTO.class));
                }
                return ResponseEntity.notFound().build();
        }

        @GetMapping("/{customerId}/loan-applications")
        @Operation(summary = "Get loan applications for a customer", description = "Retrieves all loan applications submitted by a specific customer.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved loan applications"),
                        @ApiResponse(responseCode = "404", description = "Customer not found")
        })
        public ResponseEntity<List<LoanApplicationDTO>> getCustomerLoanApplications(
                        @Parameter(description = "ID of the customer whose loan applications are to be retrieved") @PathVariable Long customerId) {
                List<LoanApplication> applications = customerService.getCustomerLoanApplications(customerId);
                List<LoanApplicationDTO> applicationDTOs = applications.stream()
                                .map(app -> modelMapper.map(app, LoanApplicationDTO.class))
                                .collect(Collectors.toList());
                return ResponseEntity.ok(applicationDTOs);
        }
}