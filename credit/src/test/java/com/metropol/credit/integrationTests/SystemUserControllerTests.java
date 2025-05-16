package com.metropol.credit.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metropol.credit.CreditApplicationTests;
import com.metropol.credit.dtos.SystemUserDTO;
import com.metropol.credit.models.entities.SystemUser;
import com.metropol.credit.models.enums.SystemUserRole;
import com.metropol.credit.models.requests.CreateSystemUserRequest;
import com.metropol.credit.models.requests.UpdateSystemUserRequest;
import com.metropol.credit.repositories.SystemUserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SystemUserControllerTests extends CreditApplicationTests {

    private final String BASE_URL = "/api/v1/system-users";

    @Autowired
    private SystemUserRepository systemUserRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private SystemUser testUser1;
    private SystemUser testUser2;

    @Before
    public void setUpIntegrationTest() {

        systemUserRepository.deleteAll();

        testUser1 = new SystemUser();
        testUser1.setEmail("admin@example.com");
        testUser1.setPassword("password123");
        testUser1.setRole(SystemUserRole.ADMIN);
        testUser1.setIsActive(true);
        testUser1 = systemUserRepository.save(testUser1);

        testUser2 = new SystemUser();
        testUser2.setEmail("loan_officer@example.com");
        testUser2.setPassword("password456");
        testUser2.setRole(SystemUserRole.LOAN_OFFICER);
        testUser2.setIsActive(true);
        testUser2 = systemUserRepository.save(testUser2);
    }

    @After
    public void tearDownIntegrationTest() {
        systemUserRepository.deleteAll();
    }

    @Test
    public void createSystemUserSuccess() throws Exception {
        CreateSystemUserRequest request = new CreateSystemUserRequest();

        request.setPassword("securePassword!");
        request.setEmail("newapiuser@example.com");
        request.setRole(SystemUserRole.LOAN_OFFICER);
        request.setFirstName("Api");
        request.setLastName("User");

        mockMvc.perform(post(BASE_URL)
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("newapiuser@example.com")))
                .andExpect(jsonPath("$.role", is(SystemUserRole.MANAGER.name())));
    }

    @Test
    public void getSystemUserByIdFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + testUser1.getId())
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser1.getId().intValue())));

    }

    @Test
    public void getSystemUserByIdNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/9999")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllSystemUsersSuccess() throws Exception {
        mockMvc.perform(get(BASE_URL)
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[1].email").exists());
    }

    @Test
    public void updateSystemUserDetailsSuccess() throws Exception {
        UpdateSystemUserRequest updateRequest = new UpdateSystemUserRequest();
        updateRequest.setEmail("updated.existing@example.com");
        updateRequest.setFirstName("UpdatedFirst");
        updateRequest.setLastName("UpdatedLast");
        updateRequest.setRole(SystemUserRole.MANAGER);

        mockMvc.perform(put(BASE_URL + "/" + testUser1.getId())
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("updated.existing@example.com")))
                .andExpect(jsonPath("$.firstName", is("UpdatedFirst")))
                .andExpect(jsonPath("$.role", is(SystemUserRole.MANAGER.name())));

        SystemUser updatedUserFromDb = systemUserRepository.findById(testUser1.getId()).orElse(null);
        assertNotNull(updatedUserFromDb);
        assertEquals("updated.existing@example.com", updatedUserFromDb.getEmail());
    }

    @Test
    public void updateSystemUserDetailsUserNotFoundShouldReturnNotFound() throws Exception {
        UpdateSystemUserRequest updateRequest = new UpdateSystemUserRequest();
        updateRequest.setEmail("updatefail@example.com");
        updateRequest.setRole(SystemUserRole.ADMIN);

        mockMvc.perform(put(BASE_URL + "/9999")
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateSystemUserDetailsEmailExistsForAnotherUserShouldReturnBadRequest() throws Exception {
        UpdateSystemUserRequest updateRequest = new UpdateSystemUserRequest();
        updateRequest.setEmail(testUser2.getEmail());
        updateRequest.setRole(SystemUserRole.ADMIN);

        mockMvc.perform(put(BASE_URL + "/" + testUser1.getId())
                .with(tokenPostProcessor())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }
}