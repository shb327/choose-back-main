package com.choose.choose_back.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Permission {

    USER("user"),

    REGISTRATION("registration");

    private final String permission;

}
