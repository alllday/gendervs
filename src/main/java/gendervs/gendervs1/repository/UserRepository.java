package gendervs.gendervs1.repository;

import gendervs.gendervs1.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 로그인 ID로 사용자 찾기
    Optional<User> findByLoginId(String loginId);
}