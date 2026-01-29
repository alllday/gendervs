package gendervs.gendervs1.service;

import gendervs.gendervs1.domain.entity.User;
import gendervs.gendervs1.domain.enums.ContentStatus;
import gendervs.gendervs1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security가 로그인할 때 사용자 정보를 가져오는 서비스
 *
 * UserDetailsService 인터페이스를 구현하여
 * loadUserByUsername() 메서드를 통해 DB에서 사용자 정보를 조회  + UserDetails 객체로 변환
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Spring Security가 로그인할 때 자동으로 호출하는 메서드
     *
     * @param username 로그인 폼에서 입력한 사용자 ID (여기서는 loginId)
     * @return UserDetails : Spring Security가 이해할 수 있는 사용자 정보 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없을 때
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. DB에서 사용자 찾기
        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 2. 계정 상태 체크
        if (user.getContentStatus() == ContentStatus.DELETED) {
            throw new UsernameNotFoundException("삭제된 계정입니다: " + username);
        }

        // 3. 권한 설정 (ROLE_USER 또는 ROLE_ADMIN)
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getIsAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // 4. Spring Security의 UserDetails 객체로 변환하여 반환
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLoginId())              // 로그인 ID
                .password(user.getPasswordHash())         // 암호화된 비밀번호
                .authorities(authorities)                 // 권한 목록
                .accountExpired(false)                    // 계정 만료 여부
                .accountLocked(user.getContentStatus() == ContentStatus.HIDDEN)  // 계정 잠김 여부
                .credentialsExpired(false)                // 비밀번호 만료 여부
                .disabled(user.getContentStatus() != ContentStatus.ACTIVE)  // 계정 비활성화 여부
                .build();
    }
}
