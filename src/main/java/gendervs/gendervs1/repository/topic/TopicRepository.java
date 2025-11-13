package gendervs.gendervs1.repository.topic;

import gendervs.gendervs1.domain.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    // 논제 등록에는 JpaRepository의 기본 메서드(save, findById 등)만 사용
    // 추가 메서드는 목록조회, 상세조회 등 다른 기능 구현 시 필요할 때 추가 예정
}
