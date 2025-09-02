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
    public Page<Topic> searchTopics(String keyword, String category, String sortBy, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        
        // 활성화된 논제만
        builder.and(topic.status.isTrue());
        
        // 키워드 검색 (제목 또는 설명)
        if (StringUtils.hasText(keyword)) {
            builder.and(topic.title.containsIgnoreCase(keyword)
                    .or(topic.description.containsIgnoreCase(keyword)));
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
    
    @Override
    public List<Topic> findPopularTopics(int limit) {
        return queryFactory
                .selectFrom(topic)
                .leftJoin(topic.user, user).fetchJoin()
                .where(topic.status.isTrue())
                .orderBy(topic.participateCount.desc(), topic.createdAt.desc())
                .limit(limit)
                .fetch();
    }
    
    @Override
    public List<Topic> findTopicsByUserId(Long userId) {
        return queryFactory
                .selectFrom(topic)
                .leftJoin(topic.user, user).fetchJoin()
                .where(topic.user.userId.eq(userId)
                        .and(topic.status.isTrue()))
                .orderBy(topic.createdAt.desc())
                .fetch();
    }
    
    @Override
    public List<Object[]> countTopicsByCategory() {
        return queryFactory
                .select(topic.category, topic.count())
                .from(topic)
                .where(topic.status.isTrue())
                .groupBy(topic.category)
                .orderBy(topic.count().desc())
                .fetch()
                .stream()
                .map(tuple -> new Object[]{tuple.get(topic.category), tuple.get(topic.count())})
                .toList();
    }
    
    @Override
    public List<Topic> findActiveTopics(int limit) {
        return queryFactory
                .selectFrom(topic)
                .leftJoin(topic.user, user).fetchJoin()
                .where(topic.status.isTrue())
                .orderBy(
                    topic.postCount.add(topic.participateCount.multiply(2)).desc(),
                    topic.updatedAt.desc()
                )
                .limit(limit)
                .fetch();
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