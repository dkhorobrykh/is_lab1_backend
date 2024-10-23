package ru.itmo.is.lab1.repository;

import jakarta.enterprise.context.ApplicationScoped;
import ru.itmo.is.lab1.model.User;

import java.util.Optional;

@ApplicationScoped
public class UserRepository extends AbstractRepository<User, Long> {

    public UserRepository() {
        super(User.class);
    }

    public Optional<User> findByUsernameIgnoreCase(String username) {
        return entityManager.createQuery("SELECT u FROM User u WHERE LOWER(u.username) = :username", User.class)
                .setParameter("username", username.toLowerCase())
                .getResultStream()
                .findFirst();
    }
}
