package com.metropol.credit.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.requests.CustomerRegistrationRequest;
import com.metropol.credit.models.requests.UpdateCustomerDetailsRequest;
import com.metropol.credit.repositories.CustomerRepository;
import com.metropol.credit.repositories.LoanApplicationRepository;
import com.metropol.credit.services.CustomerServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplUnitTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerRegistrationRequest registrationRequest;
    private UpdateCustomerDetailsRequest updateRequest;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Test");
        customer.setLastName("User");
        customer.setEmail("test@example.com");

        registrationRequest = new CustomerRegistrationRequest();
        registrationRequest.setFirstName("New");
        registrationRequest.setLastName("Customer");
        registrationRequest.setEmail("new@example.com");

        updateRequest = new UpdateCustomerDetailsRequest();
        updateRequest.setEmail("updated@example.com");
    }

    @Test
    void registerCustomer_Success() throws ExecutionException, InterruptedException, Exception {
        CompletableFuture<Customer> future = customerService.registerCustomer(registrationRequest);
        Customer result = future.get();

        assertNotNull(result);
        assertEquals(registrationRequest.getEmail(), result.getEmail());
        assertEquals(registrationRequest.getFirstName(), result.getFirstName());
    }

    @Test
    void findCustomerById_ReturnsEmptyOptional() {
        Optional<Customer> result = customerService.findCustomerById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void findCustomerByEmail_ReturnsEmptyOptional() {
        Optional<Customer> result = customerService.findCustomerByEmail("test@example.com");
        assertFalse(result.isPresent());
    }

    @Test
    void findAllCustomers_ReturnsEmptyList() {
        List<Customer> result = customerService.findAllCustomers();
        assertTrue(result.isEmpty());
    }

    @Test
    void updateCustomerDetails_Success() {
        Customer result = customerService.updateCustomerDetails(1L, updateRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(updateRequest.getEmail(), result.getEmail());

    }

    @Test
    void getCustomerLoanApplications_ReturnsEmptyList() {
        List<LoanApplication> result = customerService.getCustomerLoanApplications(1L);
        assertTrue(result.isEmpty());
    }
}