package gendervs.gendervs1.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationResponse {
    
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private String resourceType;
    private Long resourceId;
    private String operation;
    
    private OperationResponse(boolean success, String message, String resourceType, Long resourceId, String operation) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.operation = operation;
    }
    
    // Topic 관련 팩토리 메서드들
    public static OperationResponse topicCreated(Long topicId) {
        return new OperationResponse(true, "논제가 성공적으로 생성되었습니다", "topic", topicId, "created");
    }
    
    public static OperationResponse topicUpdated(Long topicId) {
        return new OperationResponse(true, "논제가 성공적으로 수정되었습니다", "topic", topicId, "updated");
    }
    
    public static OperationResponse topicDeleted(Long topicId) {
        return new OperationResponse(true, "논제가 성공적으로 삭제되었습니다", "topic", topicId, "deleted");
    }
    
    // Post 관련 팩토리 메서드들
    public static OperationResponse postCreated(Long postId) {
        return new OperationResponse(true, "게시글이 성공적으로 작성되었습니다", "post", postId, "created");
    }
    
    public static OperationResponse postUpdated(Long postId) {
        return new OperationResponse(true, "게시글이 성공적으로 수정되었습니다", "post", postId, "updated");
    }
    
    public static OperationResponse postDeleted(Long postId) {
        return new OperationResponse(true, "게시글이 성공적으로 삭제되었습니다", "post", postId, "deleted");
    }
    
    // Comment 관련 팩토리 메서드들
    public static OperationResponse commentCreated(Long commentId) {
        return new OperationResponse(true, "댓글이 성공적으로 작성되었습니다", "comment", commentId, "created");
    }
    
    public static OperationResponse commentUpdated(Long commentId) {
        return new OperationResponse(true, "댓글이 성공적으로 수정되었습니다", "comment", commentId, "updated");
    }
    
    public static OperationResponse commentDeleted(Long commentId) {
        return new OperationResponse(true, "댓글이 성공적으로 삭제되었습니다", "comment", commentId, "deleted");
    }
    
    // User 관련 팩토리 메서드들
    public static OperationResponse userRegistered(Long userId) {
        return new OperationResponse(true, "회원가입이 완료되었습니다", "user", userId, "registered");
    }
    
    public static OperationResponse userUpdated(Long userId) {
        return new OperationResponse(true, "회원 정보가 수정되었습니다", "user", userId, "updated");
    }
    
    // 관리자 기능 - 상태 변경 (operation에 상태값 저장)
    public static OperationResponse statusChanged(String resourceType, Long resourceId, String statusName) {
        String message = String.format("%s 상태가 \"%s\"(으)로 변경되었습니다",
                                      getResourceDisplayName(resourceType), statusName);
        return new OperationResponse(true, message, resourceType, resourceId, statusName);
    }
    
    // 에러 응답 (operation에 HTTP 상태코드 저장)
    public static OperationResponse error(String httpCode, String message) {
        return new OperationResponse(false, message, "error", null, httpCode);
    }
    
    // Helper 메서드
    private static String getResourceDisplayName(String resourceType) {
        return switch (resourceType) {
            case "topic" -> "논제";
            case "post" -> "게시글";
            case "comment" -> "댓글";
            case "user" -> "사용자";
            default -> resourceType;
        };
    }
}