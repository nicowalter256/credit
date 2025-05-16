package com.metropol.credit.models.entities;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import com.metropol.credit.models.enums.LoanApplicationStatus;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;

import lombok.Data;

@Entity
@Table(name = "loan_applications", indexes = {
        @Index(columnList = "customer_id"),
        @Index(columnList = "status"),
        @Index(columnList = "applicationDate")
})
@Data
@SequenceGenerator(name = "tbl_loan_applications_seq", allocationSize = 1)
@TypeDef(name = "pg_enum", typeClass = PostgreSQLEnumType.class)
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tbl_loan_applications_seq")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    BigDecimal amountRequested;
    String purpose;

    Integer termInMonths;

    @Enumerated(EnumType.STRING)
    @Type(type = "pg_enum")
    @Column(columnDefinition = "application_status")
    LoanApplicationStatus status;

    ZonedDateTime applicationDate;

    ZonedDateTime decisionDate;

    String rejectionReason;

    @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Loan loan;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    ZonedDateTime updatedAt;

}