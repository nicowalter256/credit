package com.metropol.credit.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AppUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final boolean enabled = true;
    private final boolean credentialsNonExpired = true;
    private final boolean accountNonLocked = true;
    private final boolean accountNonExpired = true;
    private final String password = null;
    private Long accountId = null;
    private String accountType;
    Collection<? extends GrantedAuthority> authorities = null;

    public AppUserDetails(Long accountId, String accountType,
            Collection<? extends GrantedAuthority> authorities) {

        this.accountId = accountId;
        this.authorities = authorities;
        this.accountType = accountType;
    }

    @Override
    public String getUsername() {
        return accountId.toString();
    }

}
