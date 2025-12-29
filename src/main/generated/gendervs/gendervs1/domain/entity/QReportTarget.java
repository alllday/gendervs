package gendervs.gendervs1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReportTarget is a Querydsl query type for ReportTarget
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReportTarget extends EntityPathBase<ReportTarget> {

    private static final long serialVersionUID = -1672224047L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReportTarget reportTarget = new QReportTarget("reportTarget");

    public final BooleanPath processed = createBoolean("processed");

    public final DateTimePath<java.time.LocalDateTime> processedAt = createDateTime("processedAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> reportCount = createNumber("reportCount", Integer.class);

    public final NumberPath<Long> reportTargetId = createNumber("reportTargetId", Long.class);

    public final NumberPath<Long> targetId = createNumber("targetId", Long.class);

    public final EnumPath<gendervs.gendervs1.domain.enums.TargetType> targetType = createEnum("targetType", gendervs.gendervs1.domain.enums.TargetType.class);

    public final QUser user;

    public QReportTarget(String variable) {
        this(ReportTarget.class, forVariable(variable), INITS);
    }

    public QReportTarget(Path<? extends ReportTarget> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReportTarget(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReportTarget(PathMetadata metadata, PathInits inits) {
        this(ReportTarget.class, metadata, inits);
    }

    public QReportTarget(Class<? extends ReportTarget> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

