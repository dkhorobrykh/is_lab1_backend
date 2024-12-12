package ru.itmo.is.lab1.model;

import jakarta.annotation.security.DeclareRoles;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Table(
        name = "IS_user",
        schema = "s367595"
)
@EntityListeners(AuditListener.class)
@Jacksonized
@Builder
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "is_admin")
    private boolean admin;

}
