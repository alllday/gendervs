package gendervs.gendervs1.repository;

import gendervs.gendervs1.domain.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TopicRepositoryCustom {
    
    // 목록 조회 전용 - 동적 검색 쿼리 (검색필드 추가)
    Page<Topic> searchTopics(String keyword, String category, String searchField, String sortBy, Pageable pageable);
}