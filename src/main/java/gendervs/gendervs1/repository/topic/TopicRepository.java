package gendervs.gendervs1.repository.topic;

import gendervs.gendervs1.domain.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Topic Repository
 * - JpaRepository: 기본 CRUD 메서드 제공
 * - TopicRepositoryCustom: QueryDSL을 사용한 복잡한 동적 쿼리
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>, TopicRepositoryCustom {
    // JpaRepository의 기본 메서드: save, findById, findAll, delete 등
    // TopicRepositoryCustom의 메서드: findTopicsWithFilters (QueryDSL 구현)

    /**
     * - 작성자 본인 확인이 필요한 경우 사용 (논제 상세조회, 수정, 삭제)
     * - loginId 비교를 위해 User까지 조회
     */
    @Query("SELECT t FROM Topic t " +
           "JOIN FETCH t.userProfile up " +
           "JOIN FETCH up.user " +
           "WHERE t.topicId = :topicId")
    Optional<Topic> findByIdWithUser(@Param("topicId") Long topicId);
}
