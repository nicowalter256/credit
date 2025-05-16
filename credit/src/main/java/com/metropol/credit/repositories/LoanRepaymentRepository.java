package com.metropol.credit.repositories;

import com.metropol.credit.models.entities.Loan;
import com.metropol.credit.models.entities.LoanRepayment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface LoanRepaymentRepository extends CrudRepository<LoanRepayment, Long> {

    List<LoanRepayment> findByLoan(Loan loan);

    List<LoanRepayment> findByPaymentDateBetween(ZonedDateTime startDate, ZonedDateTime endDate);

    @Query("SELECT lr FROM LoanRepayment lr WHERE lr.loan.id = :loanId AND lr.paymentMethod = :paymentMethod")
    List<LoanRepayment> findByLoanIdAndPaymentMethod(@Param("loanId") Long loanId,
            @Param("paymentMethod") String paymentMethod);

    @Query(value = "SELECT SUM(amount_paid) FROM loan_repayments WHERE loan_id = :loanId", nativeQuery = true)
    BigDecimal getTotalAmountPaidForLoanNative(@Param("loanId") Long loanId);

}