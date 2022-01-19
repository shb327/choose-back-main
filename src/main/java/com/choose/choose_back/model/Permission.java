package com.choose.choose_back.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Permission {

    USER_READ("users:read"),
    USER_WRITE("users:write"),

    REGISTRATION("registration");

    private final String permission;

}
