package com.metropol.credit.integrationTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.metropol.credit.CreditApplicationTests;
import com.metropol.credit.models.enums.LoanStatus;
import com.metropol.credit.models.requests.UpdateLoanBalanceRequest;
import com.metropol.credit.models.requests.UpdateLoanStatusRequest;

@WithMockUser(authorities = { "MANAGER" })
public class LoanControllerTests extends CreditApplicationTests {

    private final String BASE_URL = "/api/v1/loans";

    @Test
    public void getLoanById() throws Exception {
        mockMvc.perform(get(BASE_URL + "/10")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getLoansByCustomerId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/customer/3")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void getLoansByStatus() throws Exception {
        mockMvc.perform(get(BASE_URL + "/status/" + LoanStatus.ACTIVE.name())
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void updateLoanStatus() throws Exception {
        UpdateLoanStatusRequest statusRequest = new UpdateLoanStatusRequest();
        statusRequest.setNewStatus(LoanStatus.PAID_OFF);

        mockMvc.perform(put(BASE_URL + "/2/status")
                .content(objectMapper.writeValueAsString(statusRequest))
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted());
    }

    @Test
    public void updateLoanStatusLoanNotFound() throws Exception {
        UpdateLoanStatusRequest statusRequest = new UpdateLoanStatusRequest();
        statusRequest.setNewStatus(LoanStatus.PAID_OFF);

        mockMvc.perform(put(BASE_URL + "/999/status")
                .content(objectMapper.writeValueAsString(statusRequest))
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateOutstandingBalance() throws Exception {
        BigDecimal balance = new BigDecimal("1500.75");

        UpdateLoanBalanceRequest updateLoanBalanceRequest = new UpdateLoanBalanceRequest();
        updateLoanBalanceRequest.setNewBalance(balance);

        mockMvc.perform(put(BASE_URL + "/2/balance")
                .content(objectMapper.writeValueAsString(updateLoanBalanceRequest))
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }
}