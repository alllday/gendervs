package gendervs.gendervs1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_position")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_position_id")
    private Long userPositionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private TopicPosition position;

    @Column(name = "selected_at")
    private LocalDateTime selectedAt;

    @Column(name = "is_current")
    private Boolean isCurrent = true;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @ManyToOne
    @JoinColumn(name = "reason_post_id")
    private Post reasonPost;
}

