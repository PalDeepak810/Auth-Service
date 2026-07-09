package com.auth.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role {
    @Id
    private UUID id=UUID.randomUUID();
    @Column(unique = true,nullable = false)
    private String name;
}
