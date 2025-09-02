package gendervs.gendervs1.service;

import gendervs.gendervs1.domain.entity.Topic;
import gendervs.gendervs1.domain.entity.TopicPosition;
import gendervs.gendervs1.domain.entity.User;
import gendervs.gendervs1.dto.*;
import gendervs.gendervs1.dto.topic.*;
import gendervs.gendervs1.repository.TopicPositionRepository;
import gendervs.gendervs1.repository.TopicRepository;
import gendervs.gendervs1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicService {
    
    private final TopicRepository topicRepository;
    private final TopicPositionRepository topicPositionRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public OperationResponse createTopic(TopicCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        // 논제 생성
        Topic topic = new Topic();
        topic.setTitle(request.getTitle());
        topic.setDescription(request.getDescription());
        topic.setCategory(request.getCategory());
        topic.setUser(user);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());
        topic.setStatus(true);
        topic.setIsEditable(true);
        
        Topic savedTopic = topicRepository.save(topic);
        
        // 포지션 생성 (A, B, C)
        createTopicPositions(savedTopic, request);
        
        return OperationResponse.topicCreated(savedTopic.getTopicId());
    }
    
    public TopicResponse getTopicById(Long topicId) {
        Topic topic = topicRepository.findByTopicIdAndStatusTrue(topicId)
                .orElseThrow(() -> new IllegalArgumentException("논제를 찾을 수 없습니다"));
        
        return convertToTopicResponse(topic);
    }
    
    public Page<TopicListResponse> searchTopics(TopicSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        
        Page<Topic> topics = topicRepository.searchTopics(
                request.getKeyword(),
                request.getCategory(),
                request.getSortBy(),
                pageable
        );
        
        return topics.map(this::convertToTopicListResponse);
    }
    
    public List<TopicListResponse> getPopularTopics(int limit) {
        List<Topic> topics = topicRepository.findPopularTopics(limit);
        return topics.stream()
                .map(this::convertToTopicListResponse)
                .collect(Collectors.toList());
    }
    
    public List<TopicListResponse> getMyTopics(Long userId) {
        List<Topic> topics = topicRepository.findTopicsByUserId(userId);
        return topics.stream()
                .map(this::convertToTopicListResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public OperationResponse updateTopic(Long topicId, TopicUpdateRequest request, Long userId) {
        Topic topic = topicRepository.findByTopicIdAndStatusTrue(topicId)
                .orElseThrow(() -> new IllegalArgumentException("논제를 찾을 수 없습니다"));
        
        // 작성자 확인
        if (!topic.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("논제 수정 권한이 없습니다");
        }
        
        // 수정 가능 여부 확인
        if (!topic.getIsEditable()) {
            throw new IllegalArgumentException("더 이상 수정할 수 없는 논제입니다");
        }
        
        // 논제 수정
        topic.setTitle(request.getTitle());
        topic.setDescription(request.getDescription());
        topic.setCategory(request.getCategory());
        topic.setUpdatedAt(LocalDateTime.now());
        
        Topic savedTopic = topicRepository.save(topic);
        return OperationResponse.topicUpdated(savedTopic.getTopicId());
    }
    
    @Transactional
    public OperationResponse deleteTopic(Long topicId, Long userId) {
        Topic topic = topicRepository.findByTopicIdAndStatusTrue(topicId)
                .orElseThrow(() -> new IllegalArgumentException("논제를 찾을 수 없습니다"));
        
        // 작성자 확인
        if (!topic.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("논제 삭제 권한이 없습니다");
        }
        
        // Soft delete
        topic.setStatus(false);
        topic.setUpdatedAt(LocalDateTime.now());
        topicRepository.save(topic);
        
        return OperationResponse.topicDeleted(topicId);
    }
    
    @Transactional
    public void incrementViewCount(Long topicId) {
        Topic topic = topicRepository.findByTopicIdAndStatusTrue(topicId)
                .orElseThrow(() -> new IllegalArgumentException("논제를 찾을 수 없습니다"));
        
        topic.setTopicView(topic.getTopicView() + 1);
        topicRepository.save(topic);
    }
    
    private void createTopicPositions(Topic topic, TopicCreateRequest request) {
        // Position A
        TopicPosition positionA = new TopicPosition();
        positionA.setTopic(topic);
        positionA.setPositionCode("A");
        positionA.setPositionText(request.getPositionA());
        topicPositionRepository.save(positionA);
        
        // Position B
        TopicPosition positionB = new TopicPosition();
        positionB.setTopic(topic);
        positionB.setPositionCode("B");
        positionB.setPositionText(request.getPositionB());
        topicPositionRepository.save(positionB);
        
        // Position C
        TopicPosition positionC = new TopicPosition();
        positionC.setTopic(topic);
        positionC.setPositionCode("C");
        positionC.setPositionText(request.getPositionC());
        topicPositionRepository.save(positionC);
    }
    
    private TopicResponse convertToTopicResponse(Topic topic) {
        TopicResponse response = new TopicResponse();
        response.setTopicId(topic.getTopicId());
        response.setTitle(topic.getTitle());
        response.setDescription(topic.getDescription());
        response.setCategory(topic.getCategory());
        response.setAuthorName(topic.getUser().getLoginId());
        response.setAuthorId(topic.getUser().getUserId());
        response.setCreatedAt(topic.getCreatedAt());
        response.setUpdatedAt(topic.getUpdatedAt());
        response.setTopicView(topic.getTopicView());
        response.setLikeCount(topic.getLikeCount());
        response.setDislikeCount(topic.getDislikeCount());
        response.setParticipateCount(topic.getParticipateCount());
        response.setPostCount(topic.getPostCount());
        response.setStatus(topic.getStatus());
        response.setIsEditable(topic.getIsEditable());
        
        // 포지션 정보 추가
        List<TopicPosition> positions = topicPositionRepository.findByTopicTopicIdOrderByPositionCode(topic.getTopicId());
        List<TopicResponse.TopicPositionResponse> positionResponses = positions.stream()
                .map(this::convertToTopicPositionResponse)
                .collect(Collectors.toList());
        response.setPositions(positionResponses);
        
        return response;
    }
    
    private TopicResponse.TopicPositionResponse convertToTopicPositionResponse(TopicPosition position) {
        TopicResponse.TopicPositionResponse response = new TopicResponse.TopicPositionResponse();
        response.setPositionId(position.getPositionId());
        response.setPositionCode(position.getPositionCode());
        response.setPositionText(position.getPositionText());
        return response;
    }
    
    private TopicListResponse convertToTopicListResponse(Topic topic) {
        TopicListResponse response = new TopicListResponse();
        response.setTopicId(topic.getTopicId());
        response.setTitle(topic.getTitle());
        response.setDescription(topic.getDescription());
        response.setCategory(topic.getCategory());
        response.setAuthorName(topic.getUser().getLoginId());
        response.setCreatedAt(topic.getCreatedAt());
        response.setTopicView(topic.getTopicView());
        response.setLikeCount(topic.getLikeCount());
        response.setParticipateCount(topic.getParticipateCount());
        response.setPostCount(topic.getPostCount());
        return response;
    }
}