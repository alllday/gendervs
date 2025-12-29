package gendervs.gendervs1.domain.entity;

import gendervs.gendervs1.domain.enums.ContentStatus;
import gendervs.gendervs1.domain.enums.PositionCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserProfile userProfile;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Integer depth = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Integer likeCount = 0;

    private Integer dislikeCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private ContentStatus contentStatus = ContentStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private PositionCode positionCode = null;

    private Boolean isEditable = true;
}

