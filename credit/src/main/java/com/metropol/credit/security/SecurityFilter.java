package com.metropol.credit.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metropol.credit.interfaces.AuthenticationService;
import com.metropol.credit.utilities.CachedBodyHttpServletRequest;
import com.metropol.credit.utilities.Helpers;

public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    ObjectMapper objectMapper;

    private static String AUTH_HEADER = "Authorization";

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    Pattern pattern;

    static final Logger logger = LogManager.getLogger(SecurityFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String ipAddress = "";

        String authToken = null;
        if (request.getHeader(AUTH_HEADER) != null) {

            authToken = request.getHeader(AUTH_HEADER);
            if (authToken.toLowerCase().contains("bearer")) {
                authToken = authToken.replace("bearer", "").trim();
                authToken = authToken.replace("Bearer", "").trim();
            }
        }

        if (request.getHeader("X-Forwarded-For") != null) {
            ipAddress = request.getHeader("X-Forwarded-For");
        } else {
            ipAddress = request.getRemoteAddr();

        }
        if (request.getContentType() != null &&
                request.getContentType().startsWith("application")) {

            String requestBody = null;

            request = new CachedBodyHttpServletRequest(
                    (HttpServletRequest) request);

            try (ServletInputStream inputStream = request.getInputStream()) {

                requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            logger.info("Http request FROM remote ip address {} to URI {} with content type {} with body {}.",
                    ipAddress, request.getRequestURI(), request.getContentType(),
                    Helpers.maskSensitiveData(requestBody, pattern));

        }

        if (!WebSecurityConfig.FREE_URLS.matches(request)) {

            if (StringUtils.hasText(authToken)) {

                Authentication auth;
                try {

                    auth = getAuthentication(authToken);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    filterChain.doFilter(request, response);
                } catch (Exception e) {

                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

                }
            } else {

                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

            }

        } else {
            filterChain.doFilter(request, response);
        }

    }

    private Authentication getAuthentication(String idToken) throws JWTVerificationException {

        DecodedJWT decodedJWT = authenticationService.verifyToken(idToken);
        Long accountId = decodedJWT.getClaim("accountId").asLong();
        assert accountId != null;
        return new UsernamePasswordAuthenticationToken(accountId, decodedJWT);

    }

}
