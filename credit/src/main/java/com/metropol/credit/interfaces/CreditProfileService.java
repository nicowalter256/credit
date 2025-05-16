package com.metropol.credit.interfaces;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.metropol.credit.models.entities.CreditProfile;
import com.metropol.credit.models.requests.CreateOrUpdateCreditProfileRequest;
import com.metropol.credit.models.requests.UpdateCreditScoreRequest;
import com.metropol.credit.models.requests.UpdateCurrentDebtRequest;
import com.metropol.credit.models.requests.UpdateMaxLoanAmountRequest;

public interface CreditProfileService {

    CompletableFuture<CreditProfile> createOrUpdateCreditProfile(CreateOrUpdateCreditProfileRequest request);

    Optional<CreditProfile> getCreditProfileByCustomerId(Long customerId);

    Optional<CreditProfile> getCreditProfileById(Long profileId);

    CompletableFuture<CreditProfile> assessCreditWorthiness(Long customerId);

    CompletableFuture<CreditProfile> updateCreditScore(Long customerId, UpdateCreditScoreRequest request);

    CompletableFuture<CreditProfile> updateMaxLoanAmount(Long customerId, UpdateMaxLoanAmountRequest request);

    CompletableFuture<CreditProfile> updateCurrentDebt(Long customerId, UpdateCurrentDebtRequest request);

}