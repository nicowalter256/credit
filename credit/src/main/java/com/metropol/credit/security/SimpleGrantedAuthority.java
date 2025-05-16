package com.metropol.credit.security;

import org.springframework.security.core.GrantedAuthority;

public final class SimpleGrantedAuthority implements GrantedAuthority {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String role;

    public SimpleGrantedAuthority(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {

        return role;
    }

}