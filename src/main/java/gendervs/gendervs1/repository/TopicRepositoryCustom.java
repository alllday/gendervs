package gendervs.gendervs1.repository;

import gendervs.gendervs1.domain.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TopicRepositoryCustom {
    
    // 동적 검색 쿼리
    Page<Topic> searchTopics(String keyword, String category, String sortBy, Pageable pageable);
    
    // 인기 논제 조회
    List<Topic> findPopularTopics(int limit);
    
    // 사용자별 논제 조회
    List<Topic> findTopicsByUserId(Long userId);
    
    // 카테고리별 논제 수 집계
    List<Object[]> countTopicsByCategory();
    
    // 최근 활발한 논제 조회 (댓글, 참여 기준)
    List<Topic> findActiveTopics(int limit);
}