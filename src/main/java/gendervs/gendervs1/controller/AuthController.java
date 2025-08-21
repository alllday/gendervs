package gendervs.gendervs1.controller;

import gendervs.gendervs1.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        boolean isSuccess = authService.login(username, password);
        
        if (isSuccess) {
            return "로그인 성공!";
        } else {
            return "로그인 실패!";
        }
    }
}