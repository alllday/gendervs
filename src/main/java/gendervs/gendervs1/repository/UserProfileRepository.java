package gendervs.gendervs1.repository;

import gendervs.gendervs1.domain.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserProfile Repository
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    /**
     * loginId로 UserProfile 조회
     * - Security에서 이미 사용자 상태 검증하므로 User fetch 불필요
     * - Optional<UserProfile> findByUser_LoginId(@Param("loginId") String loginId); 와 동일 (언더바(_)는 내부 객체 필드 즉, 여기서는 User의 필드를 말함)
     */
    @Query("SELECT up FROM UserProfile up " +
           "JOIN up.user u " +
           "WHERE u.loginId = :loginId")
    Optional<UserProfile> findByLoginId(@Param("loginId") String loginId);
}
