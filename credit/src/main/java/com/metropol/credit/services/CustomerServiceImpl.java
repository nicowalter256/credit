package com.metropol.credit.services;

import com.metropol.credit.configurations.CustomException;
import com.metropol.credit.dtos.UserLoginDto;
import com.metropol.credit.interfaces.AuthenticationService;
import com.metropol.credit.interfaces.CustomerService;
import com.metropol.credit.models.AccessToken;
import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.entities.SystemUser;
import com.metropol.credit.models.requests.CustomerRegistrationRequest;
import com.metropol.credit.models.requests.UpdateCustomerDetailsRequest;
import com.metropol.credit.repositories.CustomerRepository;
import com.metropol.credit.repositories.LoanApplicationRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    LoanApplicationRepository loanApplicationRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Async
    @Override
    public CompletableFuture<Customer> registerCustomer(CustomerRegistrationRequest registrationRequest)
            throws Exception {

        if (customerRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + registrationRequest.getEmail());
        }

        Map<String, String> generated = authenticationService
                .generateUserPassword(registrationRequest.getPassword());

        Customer customer = new Customer();
        customer.setEmail(registrationRequest.getEmail());
        customer.setFirstName(registrationRequest.getFirstName());
        customer.setLastName(registrationRequest.getLastName());
        customer.setPhoneNumber(registrationRequest.getPhone());
        customer.setPassword(generated.get("password"));
        customer.setSalt(generated.get("salt"));
        customer.setIsActive(true);
        customer.setSmeName(registrationRequest.getSmeName());
        customer.setAddress(registrationRequest.getAddress());
        customer.setDateOfBirth(registrationRequest.getDateOfBirth());
        customer.setSmeRegistrationNumber(registrationRequest.getSmeRegistrationNumber());

        customerRepository.save(customer);

        return CompletableFuture.completedFuture(customer);
    }

    @Override
    public UserLoginDto login(String email, String password) throws NoSuchAlgorithmException, CustomException {
        Optional<Customer> value = customerRepository.findByEmail(email);

        if (value.isPresent()) {
            Customer user = value.get();
            if (user.getIsActive()) {

                if (user.getSalt() != null && user.getPassword() != null) {
                    String hashedPassword = authenticationService.getHashedPassword(password, user.getSalt());

                    if (hashedPassword.equals(user.getPassword())) {
                        UserLoginDto userDTO = convertUserToUserLoginDTO(user);

                        return userDTO;

                    }
                    throw new CustomException("Wrong email or password", HttpStatus.UNAUTHORIZED);
                }
                throw new CustomException("Wrong email or password", HttpStatus.UNAUTHORIZED);
            } else {
                throw new CustomException(
                        "Account email has not yet been verified. Verify your email to get access",
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new CustomException("This account has been disabled. Please contact support for assistance",
                    HttpStatus.UNAUTHORIZED);
        }

    }

    UserLoginDto convertUserToUserLoginDTO(Customer user) {
        ModelMapper mapper = new ModelMapper();

        UserLoginDto userDTO = mapper.map(user, UserLoginDto.class);

        AccessToken accessToken = authenticationService.createToken(user.getId(),
                "CUSTOMER",
                "CUSTOMER");
        userDTO.setAccessToken(accessToken.getAccessToken());
        userDTO.setRefreshToken(accessToken.getRefreshToken());
        userDTO.setRefreshTokenExpires(accessToken.getRefreshTokenExpires());

        return userDTO;
    }

    @Override
    public Optional<Customer> findCustomerById(Long customerId) {

        return customerRepository.findById(customerId);
    }

    @Override
    public Optional<Customer> findCustomerByEmail(String email) {

        return customerRepository.findByEmail(email);
    }

    @Override
    public List<Customer> findAllCustomers() {

        return customerRepository.findAllSmeCustomers();
    }

    @Override
    public Customer updateCustomerDetails(Long customerId, UpdateCustomerDetailsRequest customerDetailsRequest) {

        Customer customer = findCustomerById(customerId).get();

        if (StringUtils.hasText(customerDetailsRequest.getPhone())) {
            customer.setPhoneNumber(customerDetailsRequest.getPhone());

        }

        if (StringUtils.hasText(customerDetailsRequest.getAddress())) {
            customer.setAddress(customerDetailsRequest.getAddress());

        }

        if (StringUtils.hasText(customerDetailsRequest.getEmail())) {
            customer.setEmail(customerDetailsRequest.getEmail());

        }
        customerRepository.save(customer);

        return customer;
    }

    @Override
    public List<LoanApplication> getCustomerLoanApplications(Long customerId) {

        Customer customer = new Customer();
        customer.setId(customerId);
        return loanApplicationRepository.findByCustomer(customer);
    }
}