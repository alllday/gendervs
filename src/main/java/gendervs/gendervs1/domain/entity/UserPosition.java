package gendervs.gendervs1.domain.entity;

import gendervs.gendervs1.domain.enums.PositionCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPositionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PositionCode positionCode;

    @CreationTimestamp
    private LocalDateTime selectedAt;

    private Boolean isCurrent = true;

    @Column(columnDefinition = "TEXT")
    private String reason = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_post_id")
    private Post reasonPost;
}

