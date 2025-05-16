package com.metropol.credit.integrationTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.metropol.credit.CreditApplicationTests;
import com.metropol.credit.models.requests.CustomerRegistrationRequest;
import com.metropol.credit.models.requests.UpdateCustomerDetailsRequest;

public class CustomerControllerTests extends CreditApplicationTests {

    private final String BASE_URL = "/api/v1/customers";

    @Test
    public void registerCustomer() throws Exception {
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest();
        registrationRequest.setFirstName("Test");
        registrationRequest.setLastName("User");
        registrationRequest.setEmail("test.user" + System.currentTimeMillis() + "@example.com");
        registrationRequest.setPhone("1234567890");
        registrationRequest.setDateOfBirth(LocalDate.now().minusYears(25));
        registrationRequest.setAddress("123 Test Street");
        registrationRequest.setPassword("test1234");
        registrationRequest.setSmeRegistrationNumber("12BB");
        registrationRequest.setSmeName("Sugar Inc");

        mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(registrationRequest))
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted());
    }

    @Test
    public void getCustomerById() throws Exception {

        mockMvc.perform(get(BASE_URL + "/99")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getCustomerByEmail() throws Exception {
        mockMvc.perform(get(BASE_URL + "/email/nonexistent@example.com")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllCustomers() throws Exception {
        mockMvc.perform(get(BASE_URL)
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void updateCustomerDetails() throws Exception {
        UpdateCustomerDetailsRequest updateRequest = new UpdateCustomerDetailsRequest();

        updateRequest.setEmail("updated.user" + System.currentTimeMillis() + "@example.com");
        updateRequest.setPhone("0987654321");
        updateRequest.setAddress("456 Updated Ave");

        mockMvc.perform(put(BASE_URL + "/99")
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getCustomerLoanApplications() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1/loan-applications")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }
}