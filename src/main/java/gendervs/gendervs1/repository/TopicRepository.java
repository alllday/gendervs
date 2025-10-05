package gendervs.gendervs1.repository;

import gendervs.gendervs1.domain.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>, TopicRepositoryCustom {
    
    // 상세 조회 - 활성화된 논제만
    Optional<Topic> findByTopicIdAndStatusTrue(Long topicId);
}