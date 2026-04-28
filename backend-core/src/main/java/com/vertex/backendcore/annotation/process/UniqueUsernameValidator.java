package com.vertex.backendcore.annotation.process;

import com.vertex.backendcore.annotation.UniqueUsername;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.isBlank()) {
            return false;
        }

        Long id = entityManager.createQuery(
                        "SELECT e.id FROM UserEntity e WHERE e.username = :username",
                        Long.class
                )
                .setParameter("username", username)
                .getSingleResult();

        return id == null;
    }
}
