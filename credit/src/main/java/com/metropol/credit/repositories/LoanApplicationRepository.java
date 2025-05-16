package com.metropol.credit.repositories;

import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.entities.LoanApplication;
import com.metropol.credit.models.enums.LoanApplicationStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends CrudRepository<LoanApplication, Long> {

        @Query("SELECT la FROM LoanApplication la JOIN FETCH la.customer c WHERE c = :customer")
        List<LoanApplication> findByCustomer(Customer customer);

        @Query("SELECT la FROM LoanApplication la JOIN FETCH la.customer WHERE la.id = :id")
        Optional<LoanApplication> findById(Long id);

        @Query("SELECT la FROM LoanApplication la JOIN FETCH la.customer WHERE la.status = :status")
        List<LoanApplication> findByStatus(LoanApplicationStatus status);

        @Query("SELECT la FROM LoanApplication la WHERE la.status = :status AND la.applicationDate < :date")
        List<LoanApplication> findByStatusAndApplicationDateBefore(
                        @Param("status") LoanApplicationStatus status,
                        @Param("date") ZonedDateTime date);

        @Modifying
        @Transactional
        @Query("UPDATE LoanApplication la SET la.status = :status, la.decisionDate = :decisionDate, la.rejectionReason = :rejectionReason WHERE la.id = :id")
        int updateLoanApplicationStatus(@Param("id") Long id, @Param("status") LoanApplicationStatus status,
                        @Param("decisionDate") ZonedDateTime decisionDate,
                        @Param("rejectionReason") String rejectionReason);

        @Query(value = "SELECT COUNT(*) FROM loan_applications WHERE status = :status", nativeQuery = true)
        long countApplicationsByStatusNative(@Param("status") String status);

}