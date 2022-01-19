package com.choose.choose_back.model;


import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.choose.choose_back.model.Permission.USER_READ;
import static com.choose.choose_back.model.Permission.USER_WRITE;

public enum Role {
    REGISTRATION(Set.of(Permission.REGISTRATION)),
    DEFAULT(Set.of(USER_READ, USER_WRITE)),
    OWNER(Set.of(USER_READ, USER_WRITE));


    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions(){
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities(){
        return getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }

}
