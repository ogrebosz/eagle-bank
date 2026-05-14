package com.eaglebank.modules.users.api;

import com.eaglebank.security.CurrentUser;
import com.eaglebank.modules.users.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@Validated
public class UserController {

    private final UserService userService;
    private final CurrentUser currentUser;

    public UserController(UserService userService, CurrentUser currentUser) {
        this.userService = userService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @GetMapping("/{userId}")
    public UserResponse fetchUserById(
        @PathVariable @Pattern(regexp = "^usr-[A-Za-z0-9]+$") String userId
    ) {
        return userService.fetchOwnUser(userId, currentUser.id());
    }

    @PatchMapping("/{userId}")
    public UserResponse updateUserById(
        @PathVariable @Pattern(regexp = "^usr-[A-Za-z0-9]+$") String userId,
        @Valid @RequestBody UpdateUserRequest request
    ) {
        return userService.updateOwnUser(userId, currentUser.id(), request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(
        @PathVariable @Pattern(regexp = "^usr-[A-Za-z0-9]+$") String userId
    ) {
        userService.deleteOwnUser(userId, currentUser.id());
    }
}

