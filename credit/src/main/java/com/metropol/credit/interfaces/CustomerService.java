package com.metropol.credit.interfaces;

import com.metropol.credit.configurations.CustomException;
import com.metropol.credit.dtos.UserLoginDto;
import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.requests.CustomerRegistrationRequest;
import com.metropol.credit.models.requests.UpdateCustomerDetailsRequest;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CustomerService {

    CompletableFuture<Customer> registerCustomer(CustomerRegistrationRequest registrationRequest) throws Exception;

    Optional<Customer> findCustomerById(Long customerId);

    Optional<Customer> findCustomerByEmail(String email);

    List<Customer> findAllCustomers();

    UserLoginDto login(String email, String password) throws NoSuchAlgorithmException, CustomException;

    Customer updateCustomerDetails(Long customerId, UpdateCustomerDetailsRequest customerDetailsRequest);

    List<LoanApplication> getCustomerLoanApplications(Long customerId);

}