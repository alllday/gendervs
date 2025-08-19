package gendervs.gendervs1.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Integer depth = 1;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer likeCount = 0;

    private Integer dislikeCount = 0;

    @Column(length = 10)
    private String status = "active";

    @Column(nullable = false, length = 1)
    private String originPosition;

    private Boolean isEditable = true;
}

