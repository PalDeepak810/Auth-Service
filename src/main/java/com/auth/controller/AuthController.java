package com.auth.controller;

import com.auth.dto.AuthResponse;
import com.auth.dto.LoginRequest;
import com.auth.dto.MeResponse;
import com.auth.dto.RegisterRequest;
import com.auth.entity.User;
import com.auth.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @org.springframework.beans.factory.annotation.Value("${app.jwt.secret}")
    private String secret;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 1 .REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = authService.register(
                request.email(),
                request.password()
        );

        return ResponseEntity.status(201).body(
                Map.of(
                        "userId", user.getId(),
                        "email", user.getEmail()
                )
        );
    }

    // 2️. LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        String token = authService.login(
                request.email(),
                request.password()
        );

        return ResponseEntity.ok(
                new AuthResponse(
                        token,
                        Instant.now().plusSeconds(3600)
                )
        );
    }

    // 3️ .ME
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long userId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);
        Map<String, String> workspaces =
                (Map<String, String>) claims.get("workspaces");

        return ResponseEntity.ok(
                new MeResponse(userId, email, workspaces)
        );
    }
}

