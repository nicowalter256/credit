package com.metropol.credit.unitTests;

import com.metropol.credit.dtos.SystemUserDTO;
import com.metropol.credit.models.entities.SystemUser;
import com.metropol.credit.models.enums.SystemUserRole;
import com.metropol.credit.models.requests.CreateSystemUserRequest;
import com.metropol.credit.models.requests.UpdateSystemUserRequest;
import com.metropol.credit.repositories.SystemUserRepository;
import com.metropol.credit.services.SystemUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SystemUserServiceImplUnitTest {

    @Mock
    private SystemUserRepository systemUserRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SystemUserServiceImpl systemUserService;

    private SystemUser systemUser;
    private SystemUserDTO systemUserDTO;
    private CreateSystemUserRequest createRequest;
    private UpdateSystemUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        systemUser = new SystemUser();
        systemUser.setId(1L);
        systemUser.setEmail("test@example.com");
        systemUser.setPassword("hashedPassword");
        systemUser.setRole(SystemUserRole.ADMIN);
        systemUser.setIsActive(true);

        systemUserDTO = new SystemUserDTO();
        systemUserDTO.setId(1L);
        systemUserDTO.setEmail("test@example.com");
        systemUserDTO.setRole(SystemUserRole.ADMIN);
        systemUserDTO.setActive(true);

        createRequest = new CreateSystemUserRequest();
        createRequest.setPassword("password123");
        createRequest.setEmail("new@example.com");
        createRequest.setRole(SystemUserRole.LOAN_OFFICER);

        updateRequest = new UpdateSystemUserRequest();
        updateRequest.setEmail("updated@example.com");
        updateRequest.setRole(SystemUserRole.MANAGER);
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("User");
    }

    @Test
    void createSystemUser_Success() throws ExecutionException, InterruptedException, Exception {

        when(systemUserRepository.findByEmail(createRequest.getEmail())).thenReturn(Optional.empty());
        when(systemUserRepository.save(any(SystemUser.class))).thenAnswer(invocation -> {
            SystemUser userToSave = invocation.getArgument(0);
            return userToSave;
        });
        when(modelMapper.map(any(SystemUser.class), eq(SystemUserDTO.class))).thenReturn(systemUserDTO);

        CompletableFuture<SystemUserDTO> future = systemUserService.createSystemUser(createRequest);
        SystemUserDTO result = future.get();

        assertNotNull(result);
        verify(systemUserRepository).save(argThat(user -> user.getEmail().equals(createRequest.getEmail()) &&
                user.getRole().equals(SystemUserRole.LOAN_OFFICER) &&
                user.getIsActive()));
    }

    @Test
    void findSystemUserById_Found() {
        when(systemUserRepository.findById(1L)).thenReturn(Optional.of(systemUser));
        when(modelMapper.map(systemUser, SystemUserDTO.class)).thenReturn(systemUserDTO);

        Optional<SystemUserDTO> result = systemUserService.findSystemUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(systemUserDTO, result.get());
    }

    @Test
    void findSystemUserById_NotFound() {
        when(systemUserRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<SystemUserDTO> result = systemUserService.findSystemUserById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAllSystemUsers_ReturnsListOfUsers() {
        when(systemUserRepository.findAll()).thenReturn(Collections.singletonList(systemUser));
        when(modelMapper.map(systemUser, SystemUserDTO.class)).thenReturn(systemUserDTO);

        List<SystemUserDTO> results = systemUserService.findAllSystemUsers();

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(systemUserDTO, results.get(0));
    }

    @Test
    void updateSystemUserDetails_Success() throws ExecutionException, InterruptedException {
        when(systemUserRepository.findById(1L)).thenReturn(Optional.of(systemUser));
        when(systemUserRepository.findByEmail(updateRequest.getEmail())).thenReturn(Optional.empty());
        when(systemUserRepository.save(any(SystemUser.class))).thenReturn(systemUser);
        when(modelMapper.map(systemUser, SystemUserDTO.class)).thenReturn(systemUserDTO);

        CompletableFuture<SystemUserDTO> future = systemUserService.updateSystemUserDetails(1L, updateRequest);
        SystemUserDTO result = future.get();

        assertNotNull(result);
        verify(systemUserRepository).save(argThat(user -> user.getEmail().equals(updateRequest.getEmail()) &&
                user.getRole().equals(updateRequest.getRole()) &&
                user.getFirstName().equals(updateRequest.getFirstName())));
    }

    @Test
    void updateSystemUserDetails_UserNotFound_ThrowsRuntimeException() {
        when(systemUserRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            systemUserService.updateSystemUserDetails(1L, updateRequest).get();
        });
        assertTrue(exception.getMessage().contains("SystemUser not found"));
    }

    @Test
    void updateSystemUserDetails_EmailExistsForAnotherUser_ThrowsIllegalArgumentException() {
        SystemUser existingUserWithEmail = new SystemUser();
        existingUserWithEmail.setId(2L);
        existingUserWithEmail.setEmail(updateRequest.getEmail());

        when(systemUserRepository.findById(1L)).thenReturn(Optional.of(systemUser));
        when(systemUserRepository.findByEmail(updateRequest.getEmail())).thenReturn(Optional.of(existingUserWithEmail));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            systemUserService.updateSystemUserDetails(1L, updateRequest).get();
        });
        assertTrue(exception.getMessage().contains("Email already exists"));
    }

    @Test
    void updateSystemUserDetails_EmailUnchanged_Success() throws ExecutionException, InterruptedException {
        updateRequest.setEmail(systemUser.getEmail());

        when(systemUserRepository.findById(1L)).thenReturn(Optional.of(systemUser));
        when(systemUserRepository.findByEmail(systemUser.getEmail())).thenReturn(Optional.of(systemUser));
        when(systemUserRepository.save(any(SystemUser.class))).thenReturn(systemUser);
        when(modelMapper.map(systemUser, SystemUserDTO.class)).thenReturn(systemUserDTO);

        CompletableFuture<SystemUserDTO> future = systemUserService.updateSystemUserDetails(1L, updateRequest);
        SystemUserDTO result = future.get();

        assertNotNull(result);
        verify(systemUserRepository).save(systemUser);
    }
}