package gendervs.gendervs1.dto.topic;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TopicListResponse {
    
    private Long topicId;
    private String title;
    private String category;
    private LocalDateTime createdAt;
    private Integer topicView;
    private Integer postCount;
    private Integer participateCount;
    private Integer likeCount;
    private Integer dislikeCount;
    private String authorUserId;
    private String authorNickname;
}