package gendervs.gendervs1.repository.topic;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gendervs.gendervs1.domain.enums.ContentStatus;
import gendervs.gendervs1.domain.enums.SearchField;
import gendervs.gendervs1.domain.enums.TopicCategory;
import gendervs.gendervs1.dto.topic.TopicResponse;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static gendervs.gendervs1.domain.entity.QTopic.topic;
import static gendervs.gendervs1.domain.entity.QUserProfile.userProfile;

/**
 * TopicRepositoryCustom의 QueryDSL 구현체
 * Spring Data JPA가 자동으로 인식 (naming convention: {RepositoryName}Impl)
 */
public class TopicRepositoryImpl implements TopicRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public TopicRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<TopicResponse> findTopicsWithFilters(
            TopicCategory category,
            String keyword,
            SearchField searchField,
            Pageable pageable) {


        // 1. 메인 쿼리 (Projections.constructor 사용 - DTO 순수성 유지)
        // ⚠️ 주의: 파라미터 순서는 TopicResponse의 @AllArgsConstructor 순서와 정확히 일치해야 함!
        JPAQuery<TopicResponse> query = queryFactory
                .select(Projections.constructor(TopicResponse.class,
                        topic.topicId,              // Long
                        topic.title,                // String
                        topic.description,          // String
                        topic.topicCategory,        // TopicCategory
                        userProfile.userId,         // Long (userId)
                        userProfile.nickname,       // String (userNickname)
                        topic.createdAt,            // LocalDateTime
                        topic.updatedAt,            // LocalDateTime
                        topic.topicView,            // Integer
                        topic.likeCount,            // Integer
                        topic.dislikeCount,         // Integer
                        topic.participateCount,     // Integer
                        topic.postCount,            // Integer
                        topic.contentStatus,        // ContentStatus
                        topic.isEditable            // Boolean
                ))
                .from(topic)
                .innerJoin(userProfile).on(userProfile.userId.eq(topic.userProfile.userId))
                .where(
                        categoryEq(category),
                        topic.contentStatus.eq(ContentStatus.ACTIVE),  // ACTIVE만 조회 (고정)
                        keywordContains(keyword, searchField)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 2. 정렬 적용
        OrderSpecifier<?>[] orderSpecifiers = getOrderSpecifiers(pageable.getSort());
        query.orderBy(orderSpecifiers);

        // 3. 결과 조회
        List<TopicResponse> topics = query.fetch();

        // 4. COUNT 쿼리 (전체 개수 조회)
        Long total = queryFactory
                .select(topic.count())
                .from(topic)
                .innerJoin(userProfile).on(userProfile.userId.eq(topic.userProfile.userId))
                .where(
                        categoryEq(category),
                        topic.contentStatus.eq(ContentStatus.ACTIVE),  // ACTIVE만 조회 (고정)
                        keywordContains(keyword, searchField)
                )
                .fetchOne();

        return new PageImpl<>(topics, pageable, total != null ? total : 0L);
    }

    /**
     * 카테고리 필터 조건
     */
    private BooleanExpression categoryEq(TopicCategory category) {
        return category != null ? topic.topicCategory.eq(category) : null;
    }

    /**
     * 검색 조건 (제목/내용/작성자)
     */
    private BooleanExpression keywordContains(String keyword, SearchField searchField) {
        // 키워드 자체가 없는 경우는 검색기능이 의미가 없으니까 굳이 searchField를 따지지도 않음
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }

        // searchField에 따라 검색 대상 변경
        return switch (searchField) {
            case TITLE -> topic.title.contains(keyword);
            case CONTENT -> topic.description.contains(keyword);
            case AUTHOR -> userProfile.nickname.contains(keyword);
        };
    }

    /**
     * 정렬 조건 생성
     * Pageable의 Sort를 QueryDSL OrderSpecifier로 변환
     */
    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        // Order는 QueryDSL의 Order enum, order는 Sort.Order
        sort.forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "createdAt" ->
                        orders.add(new OrderSpecifier<>(direction, topic.createdAt));
                case "likeCount" ->
                        orders.add(new OrderSpecifier<>(direction, topic.likeCount));
                case "topicView" ->
                        orders.add(new OrderSpecifier<>(direction, topic.topicView));
                case "postCount" ->
                        orders.add(new OrderSpecifier<>(direction, topic.postCount));
                case "participateCount" ->
                        orders.add(new OrderSpecifier<>(direction, topic.participateCount));
            }
        });

        return orders.toArray(new OrderSpecifier[0]);
    }
}
