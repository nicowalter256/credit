package com.metropol.credit.repositories;

import com.metropol.credit.models.entities.SystemUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemUserRepository extends CrudRepository<SystemUser, Long> {

    Optional<SystemUser> findByEmail(String email);
}