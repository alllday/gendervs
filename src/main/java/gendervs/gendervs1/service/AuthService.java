package gendervs.gendervs1.service;

import gendervs.gendervs1.domain.entity.User;
import gendervs.gendervs1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    
    public boolean login(String username, String password) {
        // 1. 데이터베이스에서 사용자 찾기
        Optional<User> user = userRepository.findByLoginId(username);
        
        // 2. 사용자가 없으면 로그인 실패
        if (user.isEmpty()) {
            return false;
        }
        
        // 3. 비밀번호 확인 (일단 평문으로)
        User foundUser = user.get();
        return password.equals(foundUser.getPasswordHash());
    }
}