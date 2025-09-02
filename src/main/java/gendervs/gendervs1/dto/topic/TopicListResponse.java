package gendervs.gendervs1.dto.topic;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TopicListResponse {
    
    private Long topicId;
    private String title;
    private String description;
    private String category;
    private String authorName;
    private LocalDateTime createdAt;
    private Integer topicView;
    private Integer likeCount;
    private Integer participateCount;
    private Integer postCount;
}