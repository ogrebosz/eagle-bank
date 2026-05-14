package com.eaglebank.modules.auth.service;

import com.eaglebank.common.error.ApiException;
import com.eaglebank.modules.auth.api.LoginRequest;
import com.eaglebank.modules.auth.api.LoginResponse;
import com.eaglebank.modules.users.model.User;
import com.eaglebank.modules.users.service.UserService;
import com.eaglebank.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userService.getByEmail(request.email());
        if (!userService.passwordMatches(request.password(), user)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user.id());
        return new LoginResponse(token, "Bearer", 3600);
    }
}

