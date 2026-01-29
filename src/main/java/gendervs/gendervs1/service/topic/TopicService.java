package gendervs.gendervs1.service.topic;

import gendervs.gendervs1.domain.entity.Topic;
import gendervs.gendervs1.domain.entity.UserProfile;
import gendervs.gendervs1.domain.enums.ContentStatus;
import gendervs.gendervs1.domain.enums.SearchField;
import gendervs.gendervs1.domain.enums.SortBy;
import gendervs.gendervs1.domain.enums.TopicCategory;
import gendervs.gendervs1.dto.topic.TopicCreateRequest;
import gendervs.gendervs1.dto.topic.TopicResponse;
import gendervs.gendervs1.repository.UserProfileRepository;
import gendervs.gendervs1.repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserProfileRepository userProfileRepository;

    private static final int PAGE_SIZE = 4;
    private static final int MAX_PAGE = 1000;
    /**
     * 논제 등록
     * @param request 논제 생성 요청 DTO
     * @param userId 생성하는 사용자 ID
     * @return 생성된 논제 ID
     */
    @Transactional
    public Long createTopic(TopicCreateRequest request, Long userId) {
        // 1. UserProfile 조회 (User도 함께 Fetch Join)
        UserProfile userProfile = userProfileRepository.findByIdWithUser(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId: " + userId));

        // 2. 사용자 상태 확인 (정지된 사용자는 논제 생성 불가)
        // 이 부분때문에 쿼리에서 페치조인쓰는데 나중에 security쓰면 필요 없다는데 일단 보류
        if (userProfile.getUser().getContentStatus() != ContentStatus.ACTIVE) {
            throw new IllegalStateException("정지된 사용자는 논제를 생성할 수 없습니다.");
        }

        // 3. Topic 엔티티 생성
        Topic topic = new Topic();
        topic.setTitle(request.getTitle());
        topic.setDescription(request.getDescription());
        topic.setTopicCategory(request.getTopicCategory());
        topic.setUserProfile(userProfile);
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

    /**
     * 논제 목록 조회 (필터링 + 검색 + 페이징)
     * ACTIVE 상태의 논제만 조회
     * @param category 카테고리 필터 (null이면 전체)
     * @param keyword 검색 키워드 (null이면 검색 안함)
     * @param searchField 검색 필드 (TITLE, CONTENT, AUTHOR)
     * @param sortBy 정렬 기준 (LATEST, RECOMMEND, VIEW, POST, PARTICIPATE)
     * @param page 페이지 번호 (1부터 시작, max 1000)
     * @return 페이징된 논제 목록
     */
    public Page<TopicResponse> getTopicList(
            TopicCategory category,
            String keyword,
            SearchField searchField,
            SortBy sortBy,
            int page) {

        // 사용자 입력 검증 : max -> 페이지 음수 방지, min -> 최대 페이지 1000 제한
        page = Math.max(1, Math.min(page, MAX_PAGE));
        // 내부 변환 : 브라우저는 1부터 시작, 내부(pageable)는 0부터 시작
        int pageIndex = page - 1;

        // 정렬 기준 설정
        Sort sort = getSortBySortBy(sortBy);
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, sort);

        // 논제 목록 조회 (ACTIVE만 조회는 Repository에서 처리)
        return topicRepository.findTopicsWithFilters(
                category,
                keyword,
                searchField,
                pageable
        );
    }

    /**
     * 논제 상세 조회
     * - 조회수 증가는 Redis 기반 중복 방지 구현 후 추가 예정
     * @param topicId 논제 ID
     * @return 논제 상세 정보
     */
    @Transactional(readOnly = true)
    public TopicResponse getTopicDetail(Long topicId) {
        // 1. 논제 조회 (Fetch Join: Topic + UserProfile 1개 쿼리로 조회)
        Topic topic = topicRepository.findByIdWithUserProfile(topicId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 논제입니다. topicId: " + topicId));

        // 2. 상태 확인 (ACTIVE만 조회 가능)
        if (topic.getContentStatus() != ContentStatus.ACTIVE) {
            throw new IllegalStateException("삭제되었거나 정지된 논제입니다.");
        }

        // 3. DTO 변환 및 반환 (정적 팩토리 메서드 사용)
        return TopicResponse.from(topic);
    }

    /**
     * 정렬 기준에 따른 Sort 객체 생성
     * @param sortBy 정렬 기준 Enum
     * @return Sort 객체
     * 정렬 기준을 선택하는건 단순 필터링(카테고리, 키워드 검색...)과 달리 비즈니스 로직!
     * why? -> 보조 정렬(createdAt)을 뭘 선택할지, 기본 정렬(PARTICIPATE)은 무엇인지, 추천순(likeCount-dislikeCount) 등 정렬 기준은 도메인 정책에 의해 결정되기 때문!
     */
    private Sort getSortBySortBy(SortBy sortBy) {

        return switch (sortBy) {
            case LATEST -> Sort.by(Sort.Order.desc("createdAt")); // 최신순
            case RECOMMEND -> Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("createdAt")); // 추천순
            case VIEW -> Sort.by(Sort.Order.desc("topicView"), Sort.Order.desc("createdAt")); // 조회순
            case POST -> Sort.by(Sort.Order.desc("postCount"), Sort.Order.desc("createdAt")); // 게시글순
            case PARTICIPATE -> Sort.by(Sort.Order.desc("participateCount"), Sort.Order.desc("createdAt")); // 참여순
        };
    }
}
