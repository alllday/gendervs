package gendervs.gendervs1.repository.topic;

import gendervs.gendervs1.domain.enums.SearchField;
import gendervs.gendervs1.domain.enums.TopicCategory;
import gendervs.gendervs1.dto.topic.TopicResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Topic Repository의 커스텀 쿼리 인터페이스
 * QueryDSL을 사용한 복잡한 동적 쿼리를 정의
 */
public interface TopicRepositoryCustom {

    /**
     * 논제 목록 조회 - 동적 필터링 + 검색 + 페이징
     * ACTIVE 상태의 논제만 조회
     * @param category 카테고리 필터 (null이면 전체)
     * @param keyword 검색 키워드 (null이면 검색 안함)
     * @param searchField 검색 필드 (TITLE, CONTENT, AUTHOR)
     * @param pageable 페이징 및 정렬 정보
     * @return 페이징된 논제 DTO 목록
     */
    Page<TopicResponse> findTopicsWithFilters(
            TopicCategory category,
            String keyword,
            SearchField searchField,
            Pageable pageable
    );
}
