package gendervs.gendervs1.domain.entity;

import gendervs.gendervs1.domain.enums.ContentStatus;
import gendervs.gendervs1.domain.enums.TopicCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TopicCategory topicCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserProfile userProfile;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Integer topicView = 0;

    private Integer likeCount = 0;

    private Integer dislikeCount = 0;

    private Integer participateCount = 0;

    private Integer postCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private ContentStatus contentStatus = ContentStatus.ACTIVE;

    private Boolean isEditable = true;
}

