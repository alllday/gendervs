package gendervs.gendervs1.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security 관련 유틸리티 클래스
 *
 * 현재 로그인한 사용자 정보를 쉽게 가져올 수 있도록 도와주는 헬퍼 메서드 제공
 */
public class SecurityUtil {

    /**
     * 현재 로그인한 사용자의 username(loginId) 반환
     *
     * @return 로그인한 사용자의 username, 로그인하지 않았으면 null
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나, 인증되지 않았거나, 익명 사용자인 경우
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        // UserDetails 객체에서 username 추출
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        // String으로 username이 저장된 경우
        return principal.toString();
    }

    /**
     * 현재 로그인 여부 확인
     *
     * @return 로그인되어 있으면 true, 아니면 false
     */
    public static boolean isAuthenticated() {
        return getCurrentUsername() != null;
    }

    /**
     * 현재 사용자가 특정 권한을 가지고 있는지 확인
     *
     * @param role 확인할 권한 (예: "ROLE_ADMIN", "ROLE_USER")
     * @return 권한이 있으면 true, 없으면 false
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
    }

    /**
     * 현재 사용자가 관리자인지 확인
     *
     * @return 관리자면 true, 아니면 false
     */
    public static boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }
}
