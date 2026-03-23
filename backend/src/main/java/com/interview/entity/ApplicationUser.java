package com.interview.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "application_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String role;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;
}
