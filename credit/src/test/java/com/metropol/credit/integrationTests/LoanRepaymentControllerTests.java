package com.metropol.credit.integrationTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.metropol.credit.CreditApplicationTests;
import com.metropol.credit.models.requests.RecordRepaymentRequest;

public class LoanRepaymentControllerTests extends CreditApplicationTests {

    private final String BASE_URL = "/api/v1/repayments";

    @Test
    public void recordRepayment() throws Exception {
        RecordRepaymentRequest repaymentRequest = new RecordRepaymentRequest();
        repaymentRequest.setLoanId(2L);
        repaymentRequest.setAmountPaid(new BigDecimal("100.50"));
        repaymentRequest.setPaymentMethod("BANK_TRANSFER");

        mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(repaymentRequest))
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted());
    }

    @Test
    public void getRepaymentById() throws Exception {

        mockMvc.perform(get(BASE_URL + "/2")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void getRepaymentsForLoan() throws Exception {
        mockMvc.perform(get(BASE_URL + "/loan/2")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void getRepaymentsByDateRange() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        mockMvc.perform(get(BASE_URL + "/date-range")
                .param("startDate", startDate.format(formatter))
                .param("endDate", endDate.format(formatter))
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void getRepaymentsByDateRangeInvalidParams() throws Exception {

        mockMvc.perform(get(BASE_URL + "/date-range")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }
}