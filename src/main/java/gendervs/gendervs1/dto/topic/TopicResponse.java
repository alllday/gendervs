package gendervs.gendervs1.dto.topic;

import gendervs.gendervs1.domain.entity.Topic;
import gendervs.gendervs1.domain.enums.ContentStatus;
import gendervs.gendervs1.domain.enums.TopicCategory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TopicResponse {

    private Long topicId;
    private String title;
    private String description;
    private TopicCategory topicCategory;
    private String topicCategoryDisplayName;
    private Long userId;
    private String userNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer topicView;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer participateCount;
    private Integer postCount;
    private ContentStatus contentStatus;
    private Boolean isEditable;

    // Entity -> DTO 변환 정적 팩토리 메서드
    public static TopicResponse from(Topic topic) {
        TopicResponse response = new TopicResponse();
        response.setTopicId(topic.getTopicId());
        response.setTitle(topic.getTitle());
        response.setDescription(topic.getDescription());
        response.setTopicCategory(topic.getTopicCategory());
        response.setTopicCategoryDisplayName(topic.getTopicCategory().getDisplayName());

        if (topic.getUser() != null) {
            response.setUserId(topic.getUser().getUserId());
            // UserProfile이 있다면 nickname도 설정 (나중에 추가 가능)
        }

        response.setCreatedAt(topic.getCreatedAt());
        response.setUpdatedAt(topic.getUpdatedAt());
        response.setTopicView(topic.getTopicView());
        response.setLikeCount(topic.getLikeCount());
        response.setDislikeCount(topic.getDislikeCount());
        response.setParticipateCount(topic.getParticipateCount());
        response.setPostCount(topic.getPostCount());
        response.setContentStatus(topic.getContentStatus());
        response.setIsEditable(topic.getIsEditable());

        return response;
    }
}
