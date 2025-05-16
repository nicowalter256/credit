package com.metropol.credit.integrationTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.metropol.credit.CreditApplicationTests;
import com.metropol.credit.models.enums.LoanApplicationStatus;
import com.metropol.credit.models.requests.LoanApplicationRequest;

public class LoanApplicationControllerTests extends CreditApplicationTests {

    private final String BASE_URL = "/api/v1/loan-applications";

    @Test
    public void submitLoanApplication() throws Exception {
        LoanApplicationRequest applicationRequest = new LoanApplicationRequest();
        applicationRequest.setCustomerId(3L);
        applicationRequest.setAmountRequested(new BigDecimal("5000.00"));
        applicationRequest.setPurpose("Holiday Trip");
        applicationRequest.setTermInMonths(6);

        mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(applicationRequest))
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted());
    }

    @Test
    public void getLoanApplicationById() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getLoanApplicationsByCustomerId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/customer/3")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void getLoanApplicationsByStatus() throws Exception {
        mockMvc.perform(get(BASE_URL + "/status/" + LoanApplicationStatus.PENDING.name())
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void approveLoanApplication() throws Exception {
        mockMvc.perform(post(BASE_URL + "/1/approve")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void rejectLoanApplication() throws Exception {
        String reason = "{\"reason\":\"Insufficient credit score\"}";

        mockMvc.perform(post(BASE_URL + "/1/reject")
                .content(reason)
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void rejectLoanApplicationNoReason() throws Exception {
        mockMvc.perform(post(BASE_URL + "/1/reject")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void cancelLoanApplication() throws Exception {

        mockMvc.perform(post(BASE_URL + "/1/cancel")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }
}