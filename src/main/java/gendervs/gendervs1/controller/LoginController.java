package gendervs.gendervs1.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인 컨트롤러
 *
 * Spring Security가 로그인 처리를 자동으로 수행하므로
 * 이 컨트롤러는 로그인 페이지 표시와 실패 처리만 담당합니다.
 *
 * 실제 로그인 처리는 SecurityConfig에 설정된 대로 동작:
 * - POST /login: Spring Security가 자동 처리
 * - 성공 시: /topics로 리다이렉트
 * - 실패 시: POST /login으로 포워드 (URL 변경 없음)
 */
@Controller
public class LoginController {

    /**
     * 로그인 페이지 표시 (GET)
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * 로그인 실패 처리 (POST)
     * failureForwardUrl("/login/error")로 포워드되어 URL 변경 없이 에러 메시지 표시
     * /login과 분리해야 Spring Security가 다시 가로채지 않음
     */
    @PostMapping("/login/error")
    public String loginFail(HttpServletRequest request, Model model) {
        model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        model.addAttribute("username", request.getParameter("username"));
        return "login";
    }
}
