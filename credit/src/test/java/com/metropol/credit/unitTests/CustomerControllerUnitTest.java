package com.metropol.credit.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

import com.metropol.credit.controllers.CustomerController;
import com.metropol.credit.dtos.CustomerDTO;
import com.metropol.credit.dtos.LoanApplicationDTO;
import com.metropol.credit.models.Message;
import com.metropol.credit.interfaces.CustomerService;
import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.requests.CustomerRegistrationRequest;
import com.metropol.credit.models.requests.UpdateCustomerDetailsRequest;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerUnitTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerController customerController;

    private Customer mockCustomer;
    private CustomerDTO mockCustomerDTO;
    private CustomerRegistrationRequest registrationRequest;
    private UpdateCustomerDetailsRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setFirstName("Test");
        mockCustomer.setLastName("User");
        mockCustomer.setEmail("test@example.com");

        mockCustomerDTO = new CustomerDTO();
        mockCustomerDTO.setId(1L);
        mockCustomerDTO.setFirstName("Test");
        mockCustomerDTO.setLastName("User");
        mockCustomerDTO.setEmail("test@example.com");

        registrationRequest = new CustomerRegistrationRequest();
        registrationRequest.setFirstName("New");
        registrationRequest.setLastName("Customer");
        registrationRequest.setEmail("new@example.com");
        registrationRequest.setPhone("1234567890");
        registrationRequest.setDateOfBirth(LocalDate.now().minusYears(25));
        registrationRequest.setAddress("123 Main St");

        updateRequest = new UpdateCustomerDetailsRequest();
        updateRequest.setEmail("updated@example.com");

        when(modelMapper.map(any(Customer.class), eq(CustomerDTO.class))).thenReturn(mockCustomerDTO);
        when(modelMapper.map(any(LoanApplication.class), eq(LoanApplicationDTO.class)))
                .thenReturn(new LoanApplicationDTO());
    }

    @Test
    void registerCustomer_ShouldReturnAccepted() throws Exception {
        when(customerService.registerCustomer(any(CustomerRegistrationRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockCustomer));

        ResponseEntity<Message> response = customerController
                .registerCustomer(registrationRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Customer registration request accepted and is being processed.", response.getBody().getMessage());
        verify(customerService).registerCustomer(any(CustomerRegistrationRequest.class));

    }

    @Test
    void getCustomerById_WhenFound_ShouldReturnOk() {
        when(customerService.findCustomerById(1L)).thenReturn(Optional.of(mockCustomer));

        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCustomerDTO, response.getBody());
        verify(customerService).findCustomerById(1L);
    }

    @Test
    void getCustomerById_WhenNotFound_ShouldReturnNotFound() {
        when(customerService.findCustomerById(99L)).thenReturn(Optional.empty());

        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(customerService).findCustomerById(99L);
    }

    @Test
    void getAllCustomers_ShouldReturnOkWithListOfCustomers() {
        when(customerService.findAllCustomers()).thenReturn(Collections.singletonList(mockCustomer));

        ResponseEntity<List<CustomerDTO>> response = customerController.getAllCustomers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(mockCustomerDTO, response.getBody().get(0));
        verify(customerService).findAllCustomers();
    }

    @Test
    void updateCustomerDetails_ShouldReturnOk() {
        when(customerService.updateCustomerDetails(eq(1L), any(UpdateCustomerDetailsRequest.class)))
                .thenReturn(mockCustomer);

        ResponseEntity<CustomerDTO> response = customerController.updateCustomerDetails(1L, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCustomerDTO, response.getBody());
        verify(customerService).updateCustomerDetails(1L, updateRequest);
    }

    @Test
    void getCustomerLoanApplications_ShouldReturnOk() {
        when(customerService.getCustomerLoanApplications(1L))
                .thenReturn(Collections.singletonList(new LoanApplication()));

        ResponseEntity<List<LoanApplicationDTO>> response = customerController.getCustomerLoanApplications(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get(0) instanceof LoanApplicationDTO);
        verify(customerService).getCustomerLoanApplications(1L);
    }
}