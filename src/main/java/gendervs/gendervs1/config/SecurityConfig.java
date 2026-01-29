package gendervs.gendervs1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 - 세션 기반 인증
 */
@Configuration
public class SecurityConfig {

    /**
     * PasswordEncoder 빈 등록
     * BCrypt: 비밀번호를 안전하게 암호화하는 알고리즘
     * - 같은 비밀번호라도 매번 다른 해시값 생성 (Salt 자동 추가)
     * - 단방향 암호화 (복호화 불가능)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 설정 - 세션 기반 인증
     *
     * 주요 기능:
     * 1. URL별 접근 권한 설정
     * 2. 폼 기반 로그인 (쿠키/세션 자동 관리)
     * 3. 로그아웃 처리
     * 4. 세션 관리
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // URL별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                // 누구나 접근 가능한 페이지
                .requestMatchers("/", "/login", "/login/error", "/register", "/topics", "/topics/**").permitAll()
                // CSS, JS, 이미지 등 정적 리소스는 누구나 접근 가능
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                // 관리자만 접근 가능한 페이지 (나중에 추가)
                // .requestMatchers("/admin/**").hasRole("ADMIN")
                // 나머지는 로그인 필요
                .anyRequest().authenticated()
            )

            // 폼 기반 로그인 설정
            .formLogin(form -> form
                .loginPage("/login")              // 로그인 페이지 URL
                .loginProcessingUrl("/login")     // 로그인 처리 URL (form action)
                .usernameParameter("username")    // 로그인 폼의 username 필드 name
                .passwordParameter("password")    // 로그인 폼의 password 필드 name
                .defaultSuccessUrl("/topics", true)  // 로그인 성공 시 이동할 페이지
                .failureForwardUrl("/login/error") // 로그인 실패 시 포워드 (URL 변경 없음)
                .permitAll()                      // 로그인 페이지는 누구나 접근 가능
            )

            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/logout")             // 로그아웃 URL
                .logoutSuccessUrl("/")  // 로그아웃 성공 시 이동할 페이지
                .invalidateHttpSession(true)      // 세션 무효화
                .deleteCookies("JSESSIONID")      // 쿠키 삭제
                .permitAll()                      // 로그아웃은 누구나 가능
            )

            // 세션 관리
            .sessionManagement(session -> session
                .maximumSessions(1)               // 동시 로그인 세션 1개로 제한
                .maxSessionsPreventsLogin(false)  // false - 기존 세션 만료 (true면 새 로그인 차단)
            );

        return http.build();
    }
}
