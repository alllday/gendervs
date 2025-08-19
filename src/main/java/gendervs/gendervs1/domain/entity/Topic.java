package gendervs.gendervs1.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topicId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    private String category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer topicView = 0;

    private Integer likeCount = 0;

    private Integer dislikeCount = 0;

    private Integer participateCount = 0;

    private Integer postCount = 0;

    private Boolean status = true;

    private Boolean isEditable = true;
}

