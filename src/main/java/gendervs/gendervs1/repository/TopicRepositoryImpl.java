package gendervs.gendervs1.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gendervs.gendervs1.domain.entity.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static gendervs.gendervs1.domain.entity.QTopic.topic;
import static gendervs.gendervs1.domain.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class TopicRepositoryImpl implements TopicRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public Page<Topic> searchTopics(String keyword, String category, String searchField, String sortBy, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        
        // 활성화된 논제만
        builder.and(topic.status.isTrue());
        
        // 키워드 검색 (검색 필드에 따라 분기)
        if (StringUtils.hasText(keyword)) {
            switch (searchField) {
                case "title" -> builder.and(topic.title.containsIgnoreCase(keyword));
                case "content" -> builder.and(topic.description.containsIgnoreCase(keyword));
                case "author" -> builder.and(topic.user.loginId.containsIgnoreCase(keyword));
                default -> builder.and(topic.title.containsIgnoreCase(keyword)); // 기본값: 제목 검색
            }
        }
        
        // 카테고리 필터
        if (StringUtils.hasText(category)) {
            builder.and(topic.category.eq(category));
        }
        
        // 정렬 조건
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortBy);
        
        // 쿼리 실행
        JPAQuery<Topic> query = queryFactory
                .selectFrom(topic)
                .leftJoin(topic.user, user).fetchJoin()
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        
        List<Topic> content = query.fetch();
        
        // 전체 개수 조회
        JPAQuery<Long> countQuery = queryFactory
                .select(topic.count())
                .from(topic)
                .where(builder);
        
        Long total = countQuery.fetchOne();
        
        return new PageImpl<>(content, pageable, total);
    }
    
    
    private OrderSpecifier<?> getOrderSpecifier(String sortBy) {
        if ("popular".equals(sortBy)) {
            return topic.participateCount.desc();
        } else if ("likes".equals(sortBy)) {
            return topic.likeCount.desc();
        } else if ("posts".equals(sortBy)) {
            return topic.postCount.desc();
        } else if ("views".equals(sortBy)) {
            return topic.topicView.desc();
        } else {
            return topic.createdAt.desc(); // 기본값: 최신순
        }
    }
}