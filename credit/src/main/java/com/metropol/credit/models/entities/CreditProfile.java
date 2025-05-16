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
import javax.persistence.OneToOne;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Table(name = "credit_profiles", indexes = {

        @Index(columnList = "customer_id"),
})
@Data
@SequenceGenerator(name = "tbl_credit_profiles_seq", allocationSize = 1)
public class CreditProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tbl_credit_profiles_seq")
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", unique = true)
    Customer customer;

    Integer creditScore;

    BigDecimal maxLoanAmount;

    BigDecimal currentDebt;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    ZonedDateTime lastAssessmentDate;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    ZonedDateTime updatedAt;

}