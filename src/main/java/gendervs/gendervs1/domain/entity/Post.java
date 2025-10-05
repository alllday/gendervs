package gendervs.gendervs1.domain.entity;

import gendervs.gendervs1.domain.enums.ContentStatus;
import gendervs.gendervs1.domain.enums.PositionCode;
import gendervs.gendervs1.domain.enums.PostCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic = null;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private PositionCode positionCode = null;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostCategory postCategory;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Integer postView = 0;

    private Integer likeCount = 0;

    private Integer dislikeCount = 0;

    private Integer commentCount = 0;

    private Integer influenceScore = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private ContentStatus contentStatus = ContentStatus.ACTIVE;

    private Boolean isEditable = true;
}

