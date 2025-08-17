package gendervs.gendervs1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "depth")
    private Integer depth = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "\"like\"")
    private Integer likeCount = 0;

    @Column(name = "dislike")
    private Integer dislikeCount = 0;

    @Column(name = "status", length = 10)
    private String status = "active";

    @ManyToOne
    @JoinColumn(name = "origin_position_id")
    private TopicPosition originPosition;

    @Column(name = "is_editable")
    private Boolean isEditable = true;
}

