package com.metropol.credit.models.entities;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Table(name = "loan_repayments", indexes = {
        @Index(columnList = "loan_id"),
        @Index(columnList = "paymentDate")
})
@Data
@SequenceGenerator(name = "tbl_loan_repayments_seq", allocationSize = 1)
public class LoanRepayment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tbl_loan_repayments_seq")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    Loan loan;

    BigDecimal amountPaid;
    ZonedDateTime paymentDate;
    String paymentMethod;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    ZonedDateTime updatedAt;

}