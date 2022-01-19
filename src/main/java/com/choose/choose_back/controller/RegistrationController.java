package com.choose.choose_back.controller;

import com.choose.choose_back.dto.RegisterConfirmationRequestDTO;
import com.choose.choose_back.dto.RegisterEmailRequestDTO;
import com.choose.choose_back.dto.RegisterUsernameRequestDTO;
import com.choose.choose_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;

    @PostMapping("/username")
    public ResponseEntity<?> registerUsername(@RequestBody RegisterUsernameRequestDTO request) {
        try {
            userService.registerUsername(request);
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/email")
    public void registerEmail(@RequestBody RegisterEmailRequestDTO request) {
        userService.registerEmail(request);
    }

    @PostMapping("/confirm")
    public void registerConfirmation(@RequestBody RegisterConfirmationRequestDTO request) {
        userService.registerConfirm(request);
    }
}
