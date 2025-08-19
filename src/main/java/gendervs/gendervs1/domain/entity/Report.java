package gendervs.gendervs1.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports", uniqueConstraints = @UniqueConstraint(columnNames = {"reporter_id", "target_type", "target_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 10)
    private String targetType;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false, length = 20)
    private String reasonCode;

    @Column(length = 100)
    private String reasonText;

    private LocalDateTime reportedAt;

    private Boolean processed = false;

    private LocalDateTime processedAt;

    @ManyToOne
    @JoinColumn(name = "processor_id")
    private User processor;
}