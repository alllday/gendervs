package gendervs.gendervs1.controller;

import gendervs.gendervs1.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LoginController {
    
    private final AuthService authService;
    
    // 로그인 페이지 보여주기
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password, 
                       Model model) {
        
        boolean isSuccess = authService.login(username, password);
        
        if (isSuccess) {
            return "index";  // 메인페이지로 이동
        } else {
            model.addAttribute("message", "아이디 또는 비밀번호가 틀렸습니다.");
            model.addAttribute("success", false);
            return "login";  // 실패 메시지와 함께 로그인 페이지
        }
    }
}