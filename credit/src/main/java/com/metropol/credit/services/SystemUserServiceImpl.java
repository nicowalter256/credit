package com.metropol.credit.services;

import com.metropol.credit.configurations.CustomException;
import com.metropol.credit.dtos.SystemUserDTO;
import com.metropol.credit.dtos.UserLoginDto;
import com.metropol.credit.interfaces.AuthenticationService;
import com.metropol.credit.interfaces.SystemUserService;
import com.metropol.credit.models.AccessToken;
import com.metropol.credit.models.entities.SystemUser;
import com.metropol.credit.models.requests.CreateSystemUserRequest;
import com.metropol.credit.models.requests.UpdateSystemUserRequest;
import com.metropol.credit.repositories.SystemUserRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SystemUserServiceImpl implements SystemUserService {

    @Autowired
    SystemUserRepository systemUserRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationService authenticationService;

    @Override
    public UserLoginDto login(String email, String password) throws NoSuchAlgorithmException, CustomException {
        Optional<SystemUser> value = systemUserRepository.findByEmail(email);

        if (value.isPresent()) {
            SystemUser user = value.get();
            if (user.getIsActive()) {

                if (user.getSalt() != null && user.getPassword() != null) {
                    String hashedPassword = authenticationService.getHashedPassword(password, user.getSalt());

                    if (hashedPassword.equals(user.getPassword())) {
                        UserLoginDto userDTO = convertUserToUserLoginDTO(user);

                        return userDTO;

                    }
                    throw new CustomException("Wrong email or password", HttpStatus.UNAUTHORIZED);
                }
                throw new CustomException("Wrong email or password", HttpStatus.UNAUTHORIZED);
            } else {
                throw new CustomException(
                        "Account email has not yet been verified. Verify your email to get access",
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new CustomException("This account has been disabled. Please contact support for assistance",
                    HttpStatus.UNAUTHORIZED);
        }

    }

    UserLoginDto convertUserToUserLoginDTO(SystemUser user) {
        ModelMapper mapper = new ModelMapper();

        UserLoginDto userDTO = mapper.map(user, UserLoginDto.class);

        AccessToken accessToken = authenticationService.createToken(user.getId(),
                "ADMINISTRATOR",
                user.getRole().toString());
        userDTO.setAccessToken(accessToken.getAccessToken());
        userDTO.setRefreshToken(accessToken.getRefreshToken());
        userDTO.setRefreshTokenExpires(accessToken.getRefreshTokenExpires());

        return userDTO;
    }

    @Async
    @Override
    @Transactional
    public CompletableFuture<SystemUserDTO> createSystemUser(CreateSystemUserRequest request) throws Exception {

        if (systemUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        Map<String, String> generated = authenticationService
                .generateUserPassword(request.getPassword());

        SystemUser systemUser = new SystemUser();
        systemUser.setPassword(generated.get("password"));
        systemUser.setSalt(generated.get("salt"));
        systemUser.setFirstName(request.getFirstName());
        systemUser.setLastName(request.getLastName());
        systemUser.setRole(request.getRole());
        systemUser.setIsActive(true);
        systemUser.setEmail(request.getEmail());

        SystemUser savedUser = systemUserRepository.save(systemUser);
        return CompletableFuture.completedFuture(modelMapper.map(savedUser, SystemUserDTO.class));
    }

    @Override
    public Optional<SystemUserDTO> findSystemUserById(Long userId) {
        return systemUserRepository.findById(userId)
                .map(user -> modelMapper.map(user, SystemUserDTO.class));
    }

    @Override
    public List<SystemUserDTO> findAllSystemUsers() {
        return StreamSupport.stream(systemUserRepository.findAll().spliterator(), false)
                .map(user -> modelMapper.map(user, SystemUserDTO.class))
                .collect(Collectors.toList());
    }

    @Async
    @Override
    @Transactional
    public CompletableFuture<SystemUserDTO> updateSystemUserDetails(Long userId, UpdateSystemUserRequest request) {
        SystemUser systemUser = systemUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("SystemUser not found with id: " + userId));

        if (!systemUser.getEmail().equals(request.getEmail()) &&
                systemUserRepository.findByEmail(request.getEmail()).filter(u -> !u.getId().equals(userId))
                        .isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        if (StringUtils.hasText(request.getEmail())) {
            systemUser.setEmail(request.getEmail());
        }

        if (StringUtils.hasText(request.getFirstName())) {
            systemUser.setFirstName(request.getFirstName());
        }

        if (StringUtils.hasText(request.getLastName())) {
            systemUser.setLastName(request.getLastName());
        }

        if (request.getRole() != null) {
            systemUser.setRole(request.getRole());
        }

        SystemUser updatedUser = systemUserRepository.save(systemUser);
        return CompletableFuture.completedFuture(modelMapper.map(updatedUser, SystemUserDTO.class));
    }
}