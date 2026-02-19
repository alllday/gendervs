package gendervs.gendervs1.dto.topic;

import gendervs.gendervs1.domain.entity.Topic;
import gendervs.gendervs1.domain.enums.ContentStatus;
import gendervs.gendervs1.domain.enums.TopicCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 논제 응답 DTO (순수 POJO - QueryDSL 의존성 없음)
 * - 목록조회: Projections.constructor 사용 - only 조회, 성능 중시
 * - 상세조회: from() 정적 팩토리 메서드 사용 - 확장 가능성(entity 사용, 게시글 목록 ... 등)
 */
@Getter
@AllArgsConstructor
public class TopicResponse {

    private final Long topicId;
    private final String title;
    private final String description;
    private final TopicCategory topicCategory;
    private final Long userId;              // 작성자 ID (유저 페이지 링크용)
    private final String userNickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Integer topicView;
    private final Integer likeCount;
    private final Integer dislikeCount;
    private final Integer participateCount;
    private final Integer postCount;
    private final ContentStatus contentStatus;
    private final Boolean isEditable;
    private final Boolean canEdit;          // 수정/삭제 버튼 노출 여부 (본인 + isEditable)

    /**
     * 목록 조회용
     */
    public static TopicResponse from(Topic topic) {
        return from(topic, false);
    }

    /**
     * 상세 조회용 (canEdit 계산 결과 전달)
     */
    public static TopicResponse from(Topic topic, boolean canEdit) {
        return new TopicResponse(
                topic.getTopicId(),
                topic.getTitle(),
                topic.getDescription(),
                topic.getTopicCategory(),
                topic.getUserProfile().getUserId(),
                topic.getUserProfile().getNickname(),
                topic.getCreatedAt(),
                topic.getUpdatedAt(),
                topic.getTopicView(),
                topic.getLikeCount(),
                topic.getDislikeCount(),
                topic.getParticipateCount(),
                topic.getPostCount(),
                topic.getContentStatus(),
                topic.getIsEditable(),
                canEdit
        );
    }
}
