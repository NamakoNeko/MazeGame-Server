package com.javaclass.game.model;
 
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
import java.time.LocalDateTime;
 
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "admin")
public class Admin {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "account", nullable = false, unique = true, length = 50)
    private String account;
 
    @Column(name = "password", nullable = false, length = 255)
    private String password;
 
    @Column(name = "role", nullable = false, length = 20)
    private String role;
 
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}