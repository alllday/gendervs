package gendervs.gendervs1.controller;

import gendervs.gendervs1.dto.*;
import gendervs.gendervs1.dto.topic.*;
import gendervs.gendervs1.service.TopicService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {
    
    private final TopicService topicService;
    
    @PostMapping
    public ResponseEntity<OperationResponse> createTopic(
            @Valid @RequestBody TopicCreateRequest request,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(OperationResponse.error("401", "로그인이 필요합니다"));
        }
        
        OperationResponse response = topicService.createTopic(request, userId);
        return ResponseEntity.status(201).body(response);
    }
    
    @GetMapping("/{topicId}")
    public ResponseEntity<TopicResponse> getTopic(@PathVariable Long topicId) {
        TopicResponse response = topicService.getTopicById(topicId);
        
        // 조회수 증가
        topicService.incrementViewCount(topicId);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<Page<TopicListResponse>> searchTopics(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        TopicSearchRequest request = new TopicSearchRequest();
        request.setKeyword(keyword);
        request.setCategory(category);
        request.setSortBy(sortBy);
        request.setPage(page);
        request.setSize(size);
        
        Page<TopicListResponse> response = topicService.searchTopics(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<TopicListResponse>> getPopularTopics(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<TopicListResponse> response = topicService.getPopularTopics(limit);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<TopicListResponse>> getMyTopics(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        List<TopicListResponse> response = topicService.getMyTopics(userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{topicId}")
    public ResponseEntity<OperationResponse> updateTopic(
            @PathVariable Long topicId,
            @Valid @RequestBody TopicUpdateRequest request,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(OperationResponse.error("401", "로그인이 필요합니다"));
        }
        
        OperationResponse response = topicService.updateTopic(topicId, request, userId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{topicId}")
    public ResponseEntity<OperationResponse> deleteTopic(
            @PathVariable Long topicId,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(OperationResponse.error("401", "로그인이 필요합니다"));
        }
        
        OperationResponse response = topicService.deleteTopic(topicId, userId);
        return ResponseEntity.ok(response);
    }
}