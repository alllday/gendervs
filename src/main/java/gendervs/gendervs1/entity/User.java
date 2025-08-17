package gendervs.gendervs1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, length = 20)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone_encrypted", nullable = false, unique = true, length = 255)
    private String phoneEncrypted;

    @Column(name = "phone_hash", nullable = false, unique = true, length = 64)
    private String phoneHash;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status")
    private Boolean status = true;

    @Column(name = "suspend_until")
    private LocalDateTime suspendUntil;

    @Column(name = "is_admin")
    private Boolean isAdmin = false;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;
}

