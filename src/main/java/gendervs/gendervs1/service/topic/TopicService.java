package gendervs.gendervs1.service.topic;

import gendervs.gendervs1.domain.entity.Topic;
import gendervs.gendervs1.domain.entity.User;
import gendervs.gendervs1.domain.enums.ContentStatus;
import gendervs.gendervs1.dto.topic.TopicCreateRequest;
import gendervs.gendervs1.repository.UserRepository;
import gendervs.gendervs1.repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    /**
     * 논제 등록
     * @param request 논제 생성 요청 DTO
     * @param userId 생성하는 사용자 ID
     * @return 생성된 논제 ID
     */
    @Transactional
    public Long createTopic(TopicCreateRequest request, Long userId) {
        // 1. 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId: " + userId));

        // 2. 사용자 상태 확인 (정지된 사용자는 논제 생성 불가)
        if (user.getContentStatus() != ContentStatus.ACTIVE) {
            throw new IllegalStateException("정지된 사용자는 논제를 생성할 수 없습니다.");
        }

        // 3. Topic 엔티티 생성
        Topic topic = new Topic();
        topic.setTitle(request.getTitle());
        topic.setDescription(request.getDescription());
        topic.setTopicCategory(request.getTopicCategory());
        topic.setUser(user);
        topic.setContentStatus(ContentStatus.ACTIVE);
        topic.setIsEditable(true);
        topic.setTopicView(0);
        topic.setLikeCount(0);
        topic.setDislikeCount(0);
        topic.setParticipateCount(0);
        topic.setPostCount(0);

        // 4. 저장
        Topic savedTopic = topicRepository.save(topic);

        // 5. 생성된 논제 ID 반환
        return savedTopic.getTopicId();
    }
}
