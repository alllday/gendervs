package gendervs.gendervs1.controller.topic;

import gendervs.gendervs1.dto.topic.TopicCreateRequest;
import gendervs.gendervs1.service.topic.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 논제 관련 컨트롤러
 */
@Controller
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    /**
     * 논제 등록 페이지
     * GET /topics/create
     */
    @GetMapping("/create")
    public String createPage() {
        return "topic/create";
    }

    /**
     * 논제 등록 처리
     * POST /topics/create
     */
    @PostMapping("/create")
    public String createTopic(
            @Valid @ModelAttribute TopicCreateRequest request,
            BindingResult bindingResult,
            @RequestParam Long userId,
            Model model) {

        // TopicCreateRequest에 대한 검증 에러가 있으면
        if (bindingResult.hasErrors()) {
            model.addAttribute("success", false);
            model.addAttribute("message", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "topic/create";
        }

        // TopicCreateRequest외 문제들 (ex : 사용자, DB 등...)
        try {
            Long topicId = topicService.createTopic(request, userId);
            model.addAttribute("success", true);
            model.addAttribute("message", "논제가 성공적으로 등록되었습니다! (ID: " + topicId + ")");
        } catch (IllegalArgumentException e) {
            // 사용자 존재하지 않음
            model.addAttribute("success", false);
            model.addAttribute("message", e.getMessage());
        } catch (IllegalStateException e) {
            // 사용자 상태 문제
            model.addAttribute("success", false);
            model.addAttribute("message", e.getMessage());
        } catch (Exception e) {
            // 기타 예외
            model.addAttribute("success", false);
            model.addAttribute("message", "논제 등록 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "topic/create";
    }
}
