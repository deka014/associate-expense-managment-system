package com.adp.UserService.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    EMPLOYEE, MANAGER, ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
