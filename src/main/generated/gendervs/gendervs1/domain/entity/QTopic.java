package gendervs.gendervs1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTopic is a Querydsl query type for Topic
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTopic extends EntityPathBase<Topic> {

    private static final long serialVersionUID = -1875273789L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTopic topic = new QTopic("topic");

    public final EnumPath<gendervs.gendervs1.domain.enums.ContentStatus> contentStatus = createEnum("contentStatus", gendervs.gendervs1.domain.enums.ContentStatus.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Integer> dislikeCount = createNumber("dislikeCount", Integer.class);

    public final BooleanPath isEditable = createBoolean("isEditable");

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final NumberPath<Integer> participateCount = createNumber("participateCount", Integer.class);

    public final NumberPath<Integer> postCount = createNumber("postCount", Integer.class);

    public final StringPath title = createString("title");

    public final EnumPath<gendervs.gendervs1.domain.enums.TopicCategory> topicCategory = createEnum("topicCategory", gendervs.gendervs1.domain.enums.TopicCategory.class);

    public final NumberPath<Long> topicId = createNumber("topicId", Long.class);

    public final NumberPath<Integer> topicView = createNumber("topicView", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final QUserProfile userProfile;

    public QTopic(String variable) {
        this(Topic.class, forVariable(variable), INITS);
    }

    public QTopic(Path<? extends Topic> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTopic(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTopic(PathMetadata metadata, PathInits inits) {
        this(Topic.class, metadata, inits);
    }

    public QTopic(Class<? extends Topic> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userProfile = inits.isInitialized("userProfile") ? new QUserProfile(forProperty("userProfile"), inits.get("userProfile")) : null;
    }

}

