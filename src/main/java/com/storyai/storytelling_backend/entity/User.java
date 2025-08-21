package com.storyai.storytelling_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash",nullable = false, length = 255)
    private String passwordHash;


    // CONSTRUCTORS

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.passwordHash = password;
    }

    // Additional getter for compatibility
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
