package com.metropol.credit.models.entities;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.metropol.credit.models.enums.SystemUserRole;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "system_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "tbl_system_users_seq", allocationSize = 1)
@TypeDef(name = "pg_enum", typeClass = PostgreSQLEnumType.class)
public class SystemUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tbl_system_users_seq")
    Long id;
    String firstName;
    String lastName;
    String email;
    Boolean isActive = true;
    @JsonIgnore
    String password;
    @JsonIgnore
    String salt;

    @Enumerated(EnumType.STRING)
    @Type(type = "pg_enum")
    @Column(columnDefinition = "role")
    SystemUserRole role;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    ZonedDateTime updatedAt;

}