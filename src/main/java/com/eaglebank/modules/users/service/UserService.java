package com.eaglebank.modules.users.service;

import com.eaglebank.common.error.ApiException;
import com.eaglebank.modules.accounts.repository.AccountRepository;
import com.eaglebank.modules.users.api.CreateUserRequest;
import com.eaglebank.modules.users.api.UpdateUserRequest;
import com.eaglebank.modules.users.api.UserAddressDto;
import com.eaglebank.modules.users.api.UserResponse;
import com.eaglebank.modules.users.model.User;
import com.eaglebank.modules.users.model.UserAddress;
import com.eaglebank.modules.users.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       AccountRepository accountRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse create(CreateUserRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(existing -> {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already in use");
        });

        OffsetDateTime now = OffsetDateTime.now();
        User user = new User(
            generateUserId(),
            request.name(),
            mapAddress(request.address()),
            request.phoneNumber(),
            request.email().toLowerCase(),
            passwordEncoder.encode(request.password()),
            now,
            now
        );

        return toResponse(userRepository.save(user));
    }

    public UserResponse fetchOwnUser(String requestUserId, String authenticatedUserId) {
        assertOwnership(requestUserId, authenticatedUserId);
        return toResponse(getById(requestUserId));
    }

    public UserResponse updateOwnUser(String requestUserId, String authenticatedUserId, UpdateUserRequest request) {
        assertOwnership(requestUserId, authenticatedUserId);
        User existing = getById(requestUserId);

        String updatedEmail = request.email() != null ? request.email().toLowerCase() : existing.email();
        if (!updatedEmail.equals(existing.email())) {
            userRepository.findByEmail(updatedEmail).ifPresent(other -> {
                if (!other.id().equals(existing.id())) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already in use");
                }
            });
        }

        User updated = new User(
            existing.id(),
            request.name() != null ? request.name() : existing.name(),
            request.address() != null ? mapAddress(request.address()) : existing.address(),
            request.phoneNumber() != null ? request.phoneNumber() : existing.phoneNumber(),
            updatedEmail,
            request.password() != null ? passwordEncoder.encode(request.password()) : existing.passwordHash(),
            existing.createdTimestamp(),
            OffsetDateTime.now()
        );

        return toResponse(userRepository.save(updated));
    }

    public void deleteOwnUser(String requestUserId, String authenticatedUserId) {
        assertOwnership(requestUserId, authenticatedUserId);
        User existing = getById(requestUserId);

        if (!accountRepository.findByUserId(existing.id()).isEmpty()) {
            throw new ApiException(HttpStatus.CONFLICT, "A user cannot be deleted when they are associated with a bank account");
        }

        userRepository.deleteById(existing.id());
    }

    public User getById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User was not found"));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    }

    public boolean passwordMatches(String rawPassword, User user) {
        return passwordEncoder.matches(rawPassword, user.passwordHash());
    }

    private void assertOwnership(String requestUserId, String authenticatedUserId) {
        if (!requestUserId.equals(authenticatedUserId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "The user is not allowed to access this resource");
        }
    }

    private UserAddress mapAddress(UserAddressDto addressDto) {
        return new UserAddress(
            addressDto.line1(),
            addressDto.line2(),
            addressDto.line3(),
            addressDto.town(),
            addressDto.county(),
            addressDto.postcode()
        );
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
            user.id(),
            user.name(),
            new UserAddressDto(
                user.address().line1(),
                user.address().line2(),
                user.address().line3(),
                user.address().town(),
                user.address().county(),
                user.address().postcode()
            ),
            user.phoneNumber(),
            user.email(),
            user.createdTimestamp(),
            user.updatedTimestamp()
        );
    }

    private String generateUserId() {
        return "usr-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}

