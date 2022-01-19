package com.choose.choose_back;


import com.choose.choose_back.domain.postgres.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    public static User getCurrentUser() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

}
