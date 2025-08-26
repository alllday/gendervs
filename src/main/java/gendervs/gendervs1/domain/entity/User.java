package gendervs.gendervs1.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User { // 일단 현재 단방향

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true, length = 20)
    private String loginId;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 255)
    private String phoneEncrypted;

    @Column(nullable = false, unique = true, length = 64)
    private String phoneHash;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean status = true;

    private LocalDateTime suspendUntil;

    private Boolean isAdmin = false;

    private LocalDateTime lastLogin;
}

