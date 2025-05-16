package com.metropol.credit.controllers;

import com.metropol.credit.dtos.SystemUserDTO;
import com.metropol.credit.interfaces.SystemUserService;
import com.metropol.credit.models.Message;
import com.metropol.credit.models.requests.CreateSystemUserRequest;
import com.metropol.credit.models.requests.UpdateSystemUserRequest;
import com.metropol.credit.models.requests.UserLogin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/system-users")
@Tag(name = "System User Management", description = "APIs for managing internal system users (e.g., loan officers, admins)")
public class SystemUserController {

        @Autowired
        private SystemUserService systemUserService;

        @PostMapping
        @Operation(summary = "Create a new system user", description = "Registers a new internal system user.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "System user creation request accepted and is being processed"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data or username/email already exists")
        })
        public ResponseEntity<Message> createSystemUser(
                        @Valid @RequestBody CreateSystemUserRequest request) throws Exception {

                CompletableFuture<SystemUserDTO> creationFuture = systemUserService.createSystemUser(request);

                creationFuture.exceptionally(ex -> {

                        System.err.println("Async system user creation failed: " + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("System user creation request accepted and is being processed."));
        }

        /**
         * 
         * <p>
         * Returns the following status codes:
         * <ul>
         * <li>403 - when user login credentails are wrong</li>
         * <li>400 - when one of the required fields is not provided</li>
         * 
         * </ul>
         */
        @PostMapping("/login")
        ResponseEntity<?> login(@RequestBody @Valid UserLogin userLogin) throws Exception {

                return ResponseEntity.ok(systemUserService.login(userLogin.getEmail(), userLogin.getPassword()));

        }

        @GetMapping("/{userId}")
        @Operation(summary = "Get system user by ID", description = "Retrieves a specific system user by their unique ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "System user found"),
                        @ApiResponse(responseCode = "404", description = "System user not found")
        })
        public ResponseEntity<SystemUserDTO> getSystemUserById(
                        @Parameter(description = "ID of the system user to retrieve") @PathVariable Long userId) {
                return systemUserService.findSystemUserById(userId)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping
        @Operation(summary = "Get all system users", description = "Retrieves a list of all registered system users.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of system users")
        public ResponseEntity<List<SystemUserDTO>> getAllSystemUsers() {
                List<SystemUserDTO> userDTOs = systemUserService.findAllSystemUsers();
                return ResponseEntity.ok(userDTOs);
        }

        @PutMapping("/{userId}")
        @Operation(summary = "Update system user details", description = "Updates the details of an existing system user.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "System user details update request accepted and is being processed."),
                        @ApiResponse(responseCode = "400", description = "Invalid input data or email already exists"),
                        @ApiResponse(responseCode = "404", description = "System user not found")
        })
        public ResponseEntity<Message> updateSystemUserDetails(
                        @Parameter(description = "ID of the system user to update") @PathVariable Long userId,
                        @Valid @RequestBody UpdateSystemUserRequest request) {
                CompletableFuture<SystemUserDTO> updateFuture = systemUserService.updateSystemUserDetails(userId,
                                request);

                updateFuture.exceptionally(ex -> {
                        System.err.println("Async system user update failed for user ID " + userId + ": "
                                        + ex.getMessage());
                        return null;
                });

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                                .body(new Message("System user details update request for user ID " + userId
                                                + " accepted and is being processed."));
        }

}