package gendervs.gendervs1.domain.entity;

import gendervs.gendervs1.domain.enums.TargetType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_targets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportTargetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TargetType targetType;

    @Column(nullable = false)
    private Long targetId;

    private int reportCount = 0;

    private Boolean processed = false;

    @UpdateTimestamp
    private LocalDateTime processedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
