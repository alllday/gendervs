package gendervs.gendervs1.dto.topic;

import gendervs.gendervs1.domain.entity.Topic;
import gendervs.gendervs1.domain.enums.ContentStatus;
import gendervs.gendervs1.domain.enums.TopicCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 논제 응답 DTO (순수 POJO - QueryDSL 의존성 없음)
 * - 목록조회: Projections.constructor 사용
 * - 등록응답: from() 정적 팩토리 메서드 사용
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
}
