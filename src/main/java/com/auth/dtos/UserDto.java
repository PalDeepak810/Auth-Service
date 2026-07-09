package com.auth.dtos;

import com.auth.entities.Provider;
import com.auth.entities.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String email;
    private String name;
    private String password;
    private boolean enable = true;
    private Instant created_at = Instant.now();
    private Instant updated_at = Instant.now();
    private Provider provider=Provider.LOCAL;


    private Set<RoleDto> roles = new HashSet<>();

}
