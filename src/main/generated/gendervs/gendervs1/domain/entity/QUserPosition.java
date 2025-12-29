package gendervs.gendervs1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserPosition is a Querydsl query type for UserPosition
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPosition extends EntityPathBase<UserPosition> {

    private static final long serialVersionUID = 1988154624L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserPosition userPosition = new QUserPosition("userPosition");

    public final BooleanPath isCurrent = createBoolean("isCurrent");

    public final EnumPath<gendervs.gendervs1.domain.enums.PositionCode> positionCode = createEnum("positionCode", gendervs.gendervs1.domain.enums.PositionCode.class);

    public final StringPath reason = createString("reason");

    public final QPost reasonPost;

    public final DateTimePath<java.time.LocalDateTime> selectedAt = createDateTime("selectedAt", java.time.LocalDateTime.class);

    public final QTopic topic;

    public final QUser user;

    public final NumberPath<Long> userPositionId = createNumber("userPositionId", Long.class);

    public QUserPosition(String variable) {
        this(UserPosition.class, forVariable(variable), INITS);
    }

    public QUserPosition(Path<? extends UserPosition> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserPosition(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserPosition(PathMetadata metadata, PathInits inits) {
        this(UserPosition.class, metadata, inits);
    }

    public QUserPosition(Class<? extends UserPosition> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reasonPost = inits.isInitialized("reasonPost") ? new QPost(forProperty("reasonPost"), inits.get("reasonPost")) : null;
        this.topic = inits.isInitialized("topic") ? new QTopic(forProperty("topic"), inits.get("topic")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

