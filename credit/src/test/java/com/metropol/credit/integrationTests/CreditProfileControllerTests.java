package com.metropol.credit.integrationTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.metropol.credit.CreditApplicationTests;
import com.metropol.credit.models.requests.UpdateCreditScoreRequest;
import com.metropol.credit.models.requests.UpdateCurrentDebtRequest;
import com.metropol.credit.models.requests.UpdateMaxLoanAmountRequest;
import com.metropol.credit.models.requests.CreateOrUpdateCreditProfileRequest;

@WithMockUser(authorities = { "MANAGER" })
public class CreditProfileControllerTests extends CreditApplicationTests {

    private final String BASE_URL = "/api/v1/credit-profiles";

    @Test
    public void createOrUpdateCreditProfile() throws Exception {
        CreateOrUpdateCreditProfileRequest request = new CreateOrUpdateCreditProfileRequest();
        request.setCustomerId(3L);
        request.setCreditScore(700);
        request.setMaxLoanAmount(new BigDecimal("15000.00"));
        request.setCurrentDebt(new BigDecimal("2500.00"));

        mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(request))
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted());
    }

    @Test
    public void getCreditProfileByCustomerId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/customer/3")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void getCreditProfileById() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void assessCreditWorthiness() throws Exception {
        mockMvc.perform(get(BASE_URL + "/customer/3/assess")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted());
    }

    @Test
    public void updateCreditScore() throws Exception {
        UpdateCreditScoreRequest request = new UpdateCreditScoreRequest();
        request.setNewScore(720);

        mockMvc.perform(put(BASE_URL + "/customer/3/score")
                .content(objectMapper.writeValueAsString(request))
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted());
    }

    @Test
    public void updateMaxLoanAmount() throws Exception {
        UpdateMaxLoanAmountRequest request = new UpdateMaxLoanAmountRequest();
        request.setNewMaxAmount(new BigDecimal("18000.00"));

        mockMvc.perform(put(BASE_URL + "/customer/3/max-loan")
                .content(objectMapper.writeValueAsString(request))
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted());
    }

    @Test
    public void updateCurrentDebt() throws Exception {
        UpdateCurrentDebtRequest request = new UpdateCurrentDebtRequest();
        request.setNewDebtAmount(new BigDecimal("1500.00"));

        mockMvc.perform(put(BASE_URL + "/customer/3/debt")
                .content(objectMapper.writeValueAsString(request))
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted());
    }

    @Test
    public void createOrUpdateCreditProfileInvalidData() throws Exception {
        CreateOrUpdateCreditProfileRequest request = new CreateOrUpdateCreditProfileRequest();
        request.setCustomerId(3L);
        request.setCreditScore(200);
        request.setMaxLoanAmount(new BigDecimal("-100.00"));
        request.setCurrentDebt(new BigDecimal("2500.00"));

        mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(request))
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }
}