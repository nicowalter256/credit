package com.metropol.credit.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metropol.credit.models.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        if (!response.isCommitted()) {
            ObjectMapper objectMapper = new ObjectMapper();
            response.setContentType("application/json");

            switch (response.getStatus()) {

                case 400:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(objectMapper.writeValueAsString(new Message(
                            "One of the required fields is missing or is mal-formed. Please ensure your sending the required fields correctly.")));
                    break;

                case 200:
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(
                            objectMapper.writeValueAsString(new Message("access token is invalid or has expired.")));
                    break;
                case 401:

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(
                            objectMapper
                                    .writeValueAsString(new Message("access token is invalid or has expired.")));
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(objectMapper.writeValueAsString(
                            new Message("You don't have the permission required to perform this action.")));

            }

        }
    }

}