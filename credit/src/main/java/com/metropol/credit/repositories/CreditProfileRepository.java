package com.metropol.credit.repositories;

import com.metropol.credit.models.entities.CreditProfile;
import com.metropol.credit.models.entities.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditProfileRepository extends CrudRepository<CreditProfile, Long> {

        Optional<CreditProfile> findByCustomer(Customer customer);

        List<CreditProfile> findByCreditScoreGreaterThanEqual(Integer score);

        @Query("SELECT cp FROM CreditProfile cp WHERE cp.maxLoanAmount BETWEEN :minAmount AND :maxAmount")
        List<CreditProfile> findByMaxLoanAmountBetween(@Param("minAmount") BigDecimal minAmount,
                        @Param("maxAmount") BigDecimal maxAmount);

        @Modifying
        @Transactional
        @Query("UPDATE CreditProfile cp SET cp.creditScore = :newScore, cp.lastAssessmentDate = :assessmentDate WHERE cp.customer.id = :customerId")
        int updateCreditScoreForCustomer(@Param("customerId") Long customerId, @Param("newScore") Integer newScore,
                        @Param("assessmentDate") ZonedDateTime assessmentDate);

        @Query(value = "SELECT cp.* FROM credit_profiles cp JOIN customers c ON cp.customer_id = c.id WHERE c.email = :email", nativeQuery = true)
        Optional<CreditProfile> findByCustomerEmailNative(@Param("email") String email);
}