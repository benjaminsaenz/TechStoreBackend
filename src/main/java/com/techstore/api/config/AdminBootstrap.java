package com.techstore.api.config;

import com.techstore.api.model.User;
import com.techstore.api.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Ensures there is a working ADMIN user for local/dev.
 *
 * This avoids the common "login ok but admin endpoints are 403" problem when the DB has the user as USER.
 */
@Configuration
public class AdminBootstrap {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    public static final String DEFAULT_ADMIN_EMAIL = "admin@techstore.cl";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    @Bean
    CommandLineRunner ensureAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            User admin = userRepository.findByEmail(DEFAULT_ADMIN_EMAIL).orElse(null);
            if (admin == null) {
                return;
            }

            boolean changed = false;

            if (admin.getRole() != User.Role.ADMIN) {
                admin.setRole(User.Role.ADMIN);
                changed = true;
                log.warn("AdminBootstrap: user {} role was {}, fixed to ADMIN", DEFAULT_ADMIN_EMAIL, admin.getRole());
            }

            String hash = admin.getPasswordHash();
            // BCrypt hashes start with $2a$, $2b$, $2y$
            boolean looksBcrypt = hash != null && (hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"));
            if (!looksBcrypt) {
                admin.setPasswordHash(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
                changed = true;
                log.warn("AdminBootstrap: user {} password_hash was not BCrypt. Reset to default password '{}' (change it!)",
                        DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASSWORD);
            }

            if (changed) {
                userRepository.save(admin);
            }
        };
    }
}
