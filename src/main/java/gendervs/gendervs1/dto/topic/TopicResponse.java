package gendervs.gendervs1.dto.topic;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TopicResponse {
    
    private Long topicId;
    private String title;
    private String description;
    private String category;
    private String authorName;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer topicView;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer participateCount;
    private Integer postCount;
    private Boolean status;
    private Boolean isEditable;
    private List<TopicPositionResponse> positions;
    
    @Data
    public static class TopicPositionResponse {
        private Long positionId;
        private String positionCode;
        private String positionText;
    }
}