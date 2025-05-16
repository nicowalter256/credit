package com.metropol.credit.interfaces;

import com.metropol.credit.configurations.CustomException;
import com.metropol.credit.dtos.SystemUserDTO;
import com.metropol.credit.dtos.UserLoginDto;
import com.metropol.credit.models.requests.CreateSystemUserRequest;
import com.metropol.credit.models.requests.UpdateSystemUserRequest;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SystemUserService {

    CompletableFuture<SystemUserDTO> createSystemUser(CreateSystemUserRequest request) throws Exception;

    Optional<SystemUserDTO> findSystemUserById(Long userId);

    List<SystemUserDTO> findAllSystemUsers();

    CompletableFuture<SystemUserDTO> updateSystemUserDetails(Long userId, UpdateSystemUserRequest request);

    UserLoginDto login(String email, String password) throws NoSuchAlgorithmException, CustomException;
}