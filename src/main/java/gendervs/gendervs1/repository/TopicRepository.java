package gendervs.gendervs1.repository;

import gendervs.gendervs1.domain.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>, TopicRepositoryCustom {
    
    // 기본 JPA 메소드들은 JpaRepository에서 제공
    // 활성화된 논제 조회
    Optional<Topic> findByTopicIdAndStatusTrue(Long topicId);
}