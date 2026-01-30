package com.techstore.api.auth;

import com.techstore.api.auth.dto.AuthResponse;
import com.techstore.api.auth.dto.LoginRequest;
import com.techstore.api.auth.dto.RegisterRequest;
import com.techstore.api.model.User;
import com.techstore.api.repo.UserRepository;
import com.techstore.api.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    private String normEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        String email = normEmail(req.getEmail());
        String password = req.getPassword() == null ? null : req.getPassword().trim();

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email y password son obligatorios"));
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El email ya está registrado"));
        }

        User u = new User();
        u.setName(req.getName() != null && !req.getName().isBlank() ? req.getName().trim() : "Usuario");
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setAddress(req.getAddress());
        u.setRole(User.Role.USER);
        userRepository.save(u);

        String token = jwtService.generateToken(u.getEmail(), u.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, u.getEmail(), u.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String email = normEmail(req.getEmail());
        String password = req.getPassword() == null ? null : req.getPassword().trim();

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
        }

        User user = userRepository.findByEmail(email).orElseThrow();
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getRole().name()));
    }
}
