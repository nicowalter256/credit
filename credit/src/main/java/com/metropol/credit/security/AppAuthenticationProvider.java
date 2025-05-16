package com.metropol.credit.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class AppAuthenticationProvider implements AuthenticationProvider {

    Logger logger = LoggerFactory.getLogger(AppAuthenticationProvider.class);

    public AppAuthenticationProvider() {
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        DecodedJWT token = (DecodedJWT) authentication.getCredentials();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        Long accountId = token.getClaim("accountId").asLong();
        String accountType = token.getClaim("accountType").asString();
        String role = token.getClaim("role").asString();

        authorities.add(new SimpleGrantedAuthority(role));

        AppUserDetails appUserDetails = new AppUserDetails(accountId, accountType, authorities);

        return new UsernamePasswordAuthenticationToken(appUserDetails, token, appUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}