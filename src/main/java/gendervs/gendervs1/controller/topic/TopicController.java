package gendervs.gendervs1.controller.topic;

import gendervs.gendervs1.domain.enums.SearchField;
import gendervs.gendervs1.domain.enums.SortBy;
import gendervs.gendervs1.domain.enums.TopicCategory;
import gendervs.gendervs1.dto.topic.TopicRequest;
import gendervs.gendervs1.dto.topic.TopicResponse;
import gendervs.gendervs1.service.topic.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 논제 관련 컨트롤러
 */
@Controller
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final MessageSource messageSource;

    /**
     * 논제 목록 페이지
     * GET /topics
     * @param category 카테고리 필터 (선택)
     * @param keyword 검색 키워드 (선택)
     * @param searchField 검색 필드 (TITLE, CONTENT, AUTHOR)
     * @param sortBy 정렬 기준 (LATEST, RECOMMEND, VIEW, POST, PARTICIPATE)
     * @param page 페이지 번호 (기본 1)
     */
    @GetMapping
    public String listTopics(
            @RequestParam(required = false) TopicCategory category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "TITLE") SearchField searchField,
            @RequestParam(required = false, defaultValue = "PARTICIPATE") SortBy sortBy,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        try {
            // 논제 목록 조회
            Page<TopicResponse> topicPage = topicService.getTopicList(
                    category, keyword, searchField, sortBy, page
            );

            // 페이지네이션 계산 (View에서 계산하지 않도록 Controller에서 처리)
            int currentPage = topicPage.getNumber() + 1;  // 0-based -> 1-based
            int totalPages = topicPage.getTotalPages();

            // 페이지 윈도우 계산 (현재 페이지 기준 좌우 2개씩, 총 5개 표시)
            int tempStart = currentPage - 2;
            int tempEnd = currentPage + 2;

            int startPage;
            int endPage;

            if (tempStart < 1) {
                // 앞쪽 끝에 붙어있는 경우 (1, 2페이지)
                startPage = 1;
                endPage = Math.min(5, totalPages);
            } else if (tempEnd > totalPages) {
                // 뒤쪽 끝에 붙어있는 경우 (마지막, 마지막-1 페이지)
                startPage = Math.max(1, totalPages - 4);
                endPage = totalPages;
            } else {
                // 중간에 있는 경우
                startPage = tempStart;
                endPage = tempEnd;
            }

            // 모델에 데이터 추가
            model.addAttribute("topicPage", topicPage);
            model.addAttribute("categories", TopicCategory.values());
            model.addAttribute("currentCategory", category);
            model.addAttribute("currentKeyword", keyword);
            model.addAttribute("currentSearchField", searchField);
            model.addAttribute("currentSortBy", sortBy);

            // 페이지네이션 UI용 데이터
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);

            System.out.println("category = " + category + ", keyword = " + keyword + ", searchField = " + searchField + ", sortBy = " + sortBy
                    + ", page = " + topicPage.getNumber() + ", total = " + topicPage.getTotalPages() + ", size = " + topicPage.getSize());

            return "topic/list";
        } catch (Exception e) {
            model.addAttribute("error", "논제 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("categories", TopicCategory.values());

            // catch 블록에서 필수 attribute 추가 (View 보호)
            model.addAttribute("categories", TopicCategory.values());
            model.addAttribute("currentCategory", category);
            model.addAttribute("currentKeyword", keyword);
            model.addAttribute("currentSearchField", searchField);
            model.addAttribute("currentSortBy", sortBy);
            return "topic/list";
        }
    }

    /**
     * 논제 상세 페이지
     * GET /topics/{id}
     * @param id 논제 ID
     */
    @GetMapping("/{id}")
    public String detailTopic(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            // userDetails가 null일 수 있기 때문에
            String loginId = userDetails != null ? userDetails.getUsername() : null;
            TopicResponse topic = topicService.getTopicDetail(id, loginId);
            model.addAttribute("topic", topic);
            return "topic/detail";
        } catch (IllegalArgumentException | IllegalStateException e) {
            // 존재하지 않거나 접근 불가인 경우 목록으로 리다이렉트
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/topics";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "논제를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/topics";
        }
    }

    /**
     * 논제 등록 페이지
     * GET /topics/create
     */
    @GetMapping("/create")
    public String createPage() {
        return "topic/form";
    }

    /**
     * 논제 등록 처리
     * POST /topics/create
     */
    @PostMapping("/create")
    public String createTopic(
            @Valid @ModelAttribute TopicRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("success", false);
            String errorMsg = messageSource.getMessage(
                    bindingResult.getAllErrors().get(0), LocaleContextHolder.getLocale());
            model.addAttribute("message", errorMsg);
            return "topic/form";
        }

        try {
            Long topicId = topicService.createTopic(request, userDetails.getUsername());
            return "redirect:/topics/" + topicId;
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("success", false);
            model.addAttribute("message", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("message", "논제 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "topic/form";
    }

    /**
     * 논제 수정 페이지
     * GET /topics/{id}/edit
     */
    @GetMapping("/{id}/edit")
    public String editPage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            TopicResponse topic = topicService.getTopicForEdit(id, userDetails.getUsername());
            model.addAttribute("topic", topic);
            return "topic/form";
        } catch (IllegalArgumentException | IllegalStateException e) {
            // 존재하지 않거나, 수정 불가능한 경우 상세 페이지로 리다이렉트
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/topics/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "논제를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/topics/" + id;
        }
    }

    /**
     * 논제 수정 처리
     * POST /topics/{id}/edit
     */
    @PostMapping("/{id}/edit")
    public String updateTopic(
            @PathVariable Long id,
            @Valid @ModelAttribute TopicRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 검증 에러 시 사용자 입력 데이터 유지
        if (bindingResult.hasErrors()) {
            model.addAttribute("success", false);
            String errorMsg = messageSource.getMessage(
                    bindingResult.getAllErrors().get(0), LocaleContextHolder.getLocale());
            model.addAttribute("message", errorMsg);
            model.addAttribute("topicId", id);
            // request는 @ModelAttribute로 자동 바인딩되어 model에 "topicRequest"로 이미 존재
            return "topic/form";
        }

        try {
            topicService.updateTopic(id, request, userDetails.getUsername());
            return "redirect:/topics/" + id;
        } catch (IllegalArgumentException | IllegalStateException e) {
            // 수정 불가능한 경우 상세 페이지로 리다이렉트
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/topics/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "논제 수정 중 오류가 발생했습니다.");
            return "redirect:/topics/" + id;
        }
    }

    /**
     * 논제 삭제 처리
     * POST /topics/{id}/delete
     */
    @PostMapping("/{id}/delete")
    public String deleteTopic(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            topicService.deleteTopic(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("errorMessage", "논제가 삭제되었습니다.");
            return "redirect:/topics";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/topics/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "논제 삭제 중 오류가 발생했습니다.");
            return "redirect:/topics/" + id;
        }
    }
}
