package com.auth.service;

import com.auth.entity.User;
import com.auth.repository.UserRepository;
import com.auth.repository.WorkspaceMembershipRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final WorkspaceMembershipRepository membershipRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            WorkspaceMembershipRepository membershipRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(String email, String password) {
        String hash = passwordEncoder.encode(password);
        User user = new User(email, hash);
        return userRepository.save(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        Map<String, String> workspaceRoles =
                membershipRepository.findByUserId(user.getId())
                        .stream()
                        .collect(Collectors.toMap(
                                m -> m.getWorkspace().getId(),
                                m -> m.getRole().name()
                        ));

        return jwtService.generateToken(user, workspaceRoles);
    }

    public Map<String, String> getWorkspaceRoles(Long userId) {
        return membershipRepository.findByUserId(userId)
                .stream()
                .collect(Collectors.toMap(
                        m -> m.getWorkspace().getId(),
                        m -> m.getRole().name()
                ));
    }
}
