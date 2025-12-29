package gendervs.gendervs1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAdminLog is a Querydsl query type for AdminLog
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdminLog extends EntityPathBase<AdminLog> {

    private static final long serialVersionUID = -1653576095L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAdminLog adminLog = new QAdminLog("adminLog");

    public final EnumPath<gendervs.gendervs1.domain.enums.ActionType> actionType = createEnum("actionType", gendervs.gendervs1.domain.enums.ActionType.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> logId = createNumber("logId", Long.class);

    public final StringPath reason = createString("reason");

    public final NumberPath<Long> targetId = createNumber("targetId", Long.class);

    public final EnumPath<gendervs.gendervs1.domain.enums.TargetType> targetType = createEnum("targetType", gendervs.gendervs1.domain.enums.TargetType.class);

    public final QUser user;

    public QAdminLog(String variable) {
        this(AdminLog.class, forVariable(variable), INITS);
    }

    public QAdminLog(Path<? extends AdminLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAdminLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAdminLog(PathMetadata metadata, PathInits inits) {
        this(AdminLog.class, metadata, inits);
    }

    public QAdminLog(Class<? extends AdminLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

