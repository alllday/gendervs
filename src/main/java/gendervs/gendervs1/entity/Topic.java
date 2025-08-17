package gendervs.gendervs1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "topic")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long topicId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", nullable = false, length = 20)
    private String category;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "topic_view")
    private Integer topicView = 0;

    @Column(name = "\"like\"")
    private Integer likeCount = 0;

    @Column(name = "dislike")
    private Integer dislikeCount = 0;

    @Column(name = "participate")
    private Integer participateCount = 0;

    @Column(name = "post")
    private Integer postCount = 0;

    @Column(name = "status")
    private Boolean status = true;

    @Column(name = "is_editable")
    private Boolean isEditable = true;
}

