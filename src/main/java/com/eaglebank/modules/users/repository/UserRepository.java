package com.eaglebank.modules.users.repository;

import com.eaglebank.modules.users.model.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    void deleteById(String id);
}

