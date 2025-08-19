package gendervs.gendervs1.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    @Column(nullable = false, length = 20)
    private String actionType;

    @Column(nullable = false, length = 10)
    private String targetType;

    private Long targetId;

    @Column(length = 100)
    private String reason;

    private LocalDateTime createdAt;
}

