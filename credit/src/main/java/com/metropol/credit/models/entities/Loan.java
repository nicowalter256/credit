package com.metropol.credit.models.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import com.metropol.credit.models.enums.LoanStatus;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;

import lombok.Data;

@Entity
@Table(name = "loans", indexes = {
        @Index(columnList = "status"),
        @Index(columnList = "endDate"),
        @Index(columnList = "status, endDate")
})
@Data
@SequenceGenerator(name = "tbl_loans_seq", allocationSize = 1)
@TypeDef(name = "pg_enum", typeClass = PostgreSQLEnumType.class)

public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tbl_loans_seq")
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", unique = true)
    LoanApplication loanApplication;

    BigDecimal amountApproved;
    BigDecimal interestRate;
    Integer termInMonths;

    LocalDate startDate;
    LocalDate endDate;
    BigDecimal outstandingBalance;

    @Enumerated(EnumType.STRING)
    @Type(type = "pg_enum")
    @Column(columnDefinition = "loan_status")
    LoanStatus status;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    ZonedDateTime updatedAt;

}