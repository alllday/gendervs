package gendervs.gendervs1.repository;

import gendervs.gendervs1.domain.entity.TopicPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicPositionRepository extends JpaRepository<TopicPosition, Long> {
    
    List<TopicPosition> findByTopicTopicIdOrderByPositionCode(Long topicId);
    
    void deleteByTopicTopicId(Long topicId);
}