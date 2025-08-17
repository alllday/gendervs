package gendervs.gendervs1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report", uniqueConstraints = @UniqueConstraint(columnNames = {"reporter_id", "target_type", "target_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @Column(name = "target_type", nullable = false, length = 10)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "reason_code", nullable = false, length = 20)
    private String reasonCode;

    @Column(name = "reason_text", length = 100)
    private String reasonText;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    @Column(name = "processed")
    private Boolean processed = false;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @ManyToOne
    @JoinColumn(name = "processor_id")
    private User processor;
}

