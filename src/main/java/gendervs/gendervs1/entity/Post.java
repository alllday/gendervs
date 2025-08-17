package gendervs.gendervs1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private TopicPosition position;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "category", nullable = false, length = 20)
    private String category;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "post_view")
    private Integer postView = 0;

    @Column(name = "\"like\"")
    private Integer likeCount = 0;

    @Column(name = "dislike")
    private Integer dislikeCount = 0;

    @Column(name = "comment")
    private Integer commentCount = 0;

    @Column(name = "influence_score")
    private Integer influenceScore = 0;

    @Column(name = "status", length = 10)
    private String status = "active";

    @Column(name = "is_editable")
    private Boolean isEditable = true;
}

