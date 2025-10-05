package gendervs.gendervs1.dto.topic;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TopicResponse {
    
    private Long topicId;
    private String title;
    private String description;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer topicView;
    private Integer postCount;
    private Integer participateCount;
    private Integer likeCount;
    private Integer dislikeCount;
    private Boolean isEditable;
    private Boolean status;
    private Long authorUserId;
    private String authorNickname;
}