package gendervs.gendervs1.dto.topic;

import lombok.Data;

@Data
public class TopicSearchRequest {
    
    private String keyword;        // 검색 키워드 (제목, 설명)
    private String category;       // 카테고리 필터
    private String sortBy = "latest"; // 정렬 기준 (latest, popular, likes, posts, views)
    private int page = 0;          // 페이지 번호 (0부터 시작)
    private int size = 20;         // 페이지 크기
}