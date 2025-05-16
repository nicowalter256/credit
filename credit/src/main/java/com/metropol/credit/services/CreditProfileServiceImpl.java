package com.metropol.credit.services;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.metropol.credit.interfaces.CreditProfileService;
import com.metropol.credit.models.entities.CreditProfile;
import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.requests.UpdateCreditScoreRequest;
import com.metropol.credit.models.requests.UpdateCurrentDebtRequest;
import com.metropol.credit.models.requests.UpdateMaxLoanAmountRequest;
import com.metropol.credit.models.requests.CreateOrUpdateCreditProfileRequest;
import com.metropol.credit.repositories.CreditProfileRepository;
import com.metropol.credit.repositories.CustomerRepository;

@Service
@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER', 'LOAN_OFFICER')")
public class CreditProfileServiceImpl implements CreditProfileService {

    @Autowired
    private CreditProfileRepository creditProfileRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Async
    @Override
    public CompletableFuture<CreditProfile> createOrUpdateCreditProfile(
            CreateOrUpdateCreditProfileRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + request.getCustomerId()));

        Optional<CreditProfile> existingProfile = creditProfileRepository.findByCustomer(customer);

        CreditProfile profile = existingProfile.orElseGet(() -> {
            CreditProfile newProfile = new CreditProfile();
            newProfile.setCustomer(customer);
            return newProfile;
        });

        profile.setCreditScore(request.getCreditScore());
        profile.setMaxLoanAmount(request.getMaxLoanAmount());
        profile.setCurrentDebt(request.getCurrentDebt());
        profile.setLastAssessmentDate(ZonedDateTime.now());

        CreditProfile savedProfile = creditProfileRepository.save(profile);

        return CompletableFuture.completedFuture(savedProfile);
    }

    @Override
    public Optional<CreditProfile> getCreditProfileByCustomerId(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        return creditProfileRepository.findByCustomer(customer);
    }

    @Override
    public Optional<CreditProfile> getCreditProfileById(Long profileId) {

        return creditProfileRepository.findById(profileId);
    }

    @Async
    @Override
    public CompletableFuture<CreditProfile> assessCreditWorthiness(Long customerId) {

        CreditProfile profile = getCreditProfileByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Credit profile not found for customer ID: " + customerId));

        int newScore = profile.getCreditScore() + 10;
        BigDecimal newMaxLoan = profile.getMaxLoanAmount().add(new BigDecimal("1000.00"));

        profile.setCreditScore(newScore);
        profile.setMaxLoanAmount(newMaxLoan);
        profile.setLastAssessmentDate(ZonedDateTime.now());

        return CompletableFuture.completedFuture(creditProfileRepository.save(profile));
    }

    @Async
    @Override
    public CompletableFuture<CreditProfile> updateCreditScore(Long customerId, UpdateCreditScoreRequest request) {

        CreditProfile profile = getCreditProfileByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Credit profile not found for customer ID: " + customerId));

        profile.setCreditScore(request.getNewScore());
        profile.setLastAssessmentDate(ZonedDateTime.now());

        return CompletableFuture.completedFuture(creditProfileRepository.save(profile));
    }

    @Async
    @Override
    public CompletableFuture<CreditProfile> updateMaxLoanAmount(Long customerId, UpdateMaxLoanAmountRequest request) {

        CreditProfile profile = getCreditProfileByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Credit profile not found for customer ID: " + customerId));

        profile.setMaxLoanAmount(request.getNewMaxAmount());

        return CompletableFuture.completedFuture(creditProfileRepository.save(profile));
    }

    @Async
    @Override
    public CompletableFuture<CreditProfile> updateCurrentDebt(Long customerId, UpdateCurrentDebtRequest request) {

        CreditProfile profile = getCreditProfileByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Credit profile not found for customer ID: " + customerId));
        profile.setCurrentDebt(request.getNewDebtAmount());
        return CompletableFuture.completedFuture(creditProfileRepository.save(profile));
    }
}