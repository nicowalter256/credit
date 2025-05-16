package com.metropol.credit.repositories;

import com.metropol.credit.models.entities.Customer;
import com.metropol.credit.models.entities.Loan;
import com.metropol.credit.models.enums.LoanStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends CrudRepository<Loan, Long> {

    @Query("SELECT l FROM Loan l INNER JOIN l.loanApplication a WHERE a.customer = :customer")
    List<Loan> findByCustomer(@Param("customer") Customer customer);

    List<Loan> findByStatus(LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.status = :status AND l.endDate < :currentDate")
    List<Loan> findOverdueLoansByStatus(@Param("status") LoanStatus status,
            @Param("currentDate") LocalDate currentDate);

    @Modifying
    @Transactional
    @Query("UPDATE Loan l SET l.status = :newStatus WHERE l.id = :loanId")
    int updateLoanStatus(@Param("loanId") Long loanId, @Param("newStatus") LoanStatus newStatus);

    @Query(value = "SELECT * FROM loans WHERE outstanding_balance > :amount", nativeQuery = true)
    List<Loan> findLoansWithOutstandingBalanceGreaterThanNative(@Param("amount") BigDecimal amount);
}