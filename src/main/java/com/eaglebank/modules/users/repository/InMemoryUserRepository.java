package com.eaglebank.modules.users.repository;

import com.eaglebank.modules.users.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, String> userIdByEmail = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        usersById.put(user.id(), user);
        userIdByEmail.put(user.email().toLowerCase(), user.id());
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String userId = userIdByEmail.get(email.toLowerCase());
        if (userId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(usersById.get(userId));
    }

    @Override
    public void deleteById(String id) {
        User removed = usersById.remove(id);
        if (removed != null) {
            userIdByEmail.remove(removed.email().toLowerCase());
        }
    }
}

