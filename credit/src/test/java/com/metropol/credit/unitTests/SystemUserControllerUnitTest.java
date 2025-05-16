package com.metropol.credit.unitTests;

import com.metropol.credit.controllers.SystemUserController;
import com.metropol.credit.dtos.SystemUserDTO;
import com.metropol.credit.models.Message;
import com.metropol.credit.interfaces.SystemUserService;
import com.metropol.credit.models.enums.SystemUserRole;
import com.metropol.credit.models.requests.CreateSystemUserRequest;
import com.metropol.credit.models.requests.UpdateSystemUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SystemUserControllerUnitTest {

    @Mock
    private SystemUserService systemUserService;

    @InjectMocks
    private SystemUserController systemUserController;

    private SystemUserDTO systemUserDTO;
    private CreateSystemUserRequest createRequest;
    private UpdateSystemUserRequest updateRequest;

    @BeforeEach
    void setUp() {
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
    void createSystemUser_ShouldReturnAccepted() throws Exception {
        when(systemUserService.createSystemUser(any(CreateSystemUserRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(systemUserDTO));

        ResponseEntity<Message> response = systemUserController.createSystemUser(createRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("System user creation request accepted and is being processed.", response.getBody().getMessage());
        verify(systemUserService).createSystemUser(createRequest);
    }

    @Test
    void updateSystemUserDetails_ShouldReturnAccepted() throws Exception {
        Long userId = 1L;
        when(systemUserService.updateSystemUserDetails(eq(userId), any(UpdateSystemUserRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(systemUserDTO));

        ResponseEntity<Message> response = systemUserController.updateSystemUserDetails(userId, updateRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("System user details update request for user ID " + userId + " accepted and is being processed.",
                response.getBody().getMessage());
        verify(systemUserService).updateSystemUserDetails(userId, updateRequest);
    }

    @Test
    void getSystemUserById_WhenFound_ShouldReturnOk() {
        when(systemUserService.findSystemUserById(1L)).thenReturn(Optional.of(systemUserDTO));

        ResponseEntity<SystemUserDTO> response = systemUserController.getSystemUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(systemUserDTO, response.getBody());
        verify(systemUserService).findSystemUserById(1L);
    }

    @Test
    void getSystemUserById_WhenNotFound_ShouldReturnNotFound() {
        when(systemUserService.findSystemUserById(99L)).thenReturn(Optional.empty());

        ResponseEntity<SystemUserDTO> response = systemUserController.getSystemUserById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(systemUserService).findSystemUserById(99L);
    }

    @Test
    void getAllSystemUsers_ShouldReturnOkWithListOfUsers() {
        List<SystemUserDTO> userList = Collections.singletonList(systemUserDTO);
        when(systemUserService.findAllSystemUsers()).thenReturn(userList);

        ResponseEntity<List<SystemUserDTO>> response = systemUserController.getAllSystemUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(systemUserDTO, response.getBody().get(0));
        verify(systemUserService).findAllSystemUsers();
    }

}