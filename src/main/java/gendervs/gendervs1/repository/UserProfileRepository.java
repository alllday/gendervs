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
     * UserProfile 조회 (User Fetch Join)
     * - User 상태 확인을 위해 User도 함께 조회
     */
    @Query("SELECT up FROM UserProfile up " +
           "JOIN FETCH up.user " +
           "WHERE up.userId = :userId")
    Optional<UserProfile> findByIdWithUser(@Param("userId") Long userId);

}
