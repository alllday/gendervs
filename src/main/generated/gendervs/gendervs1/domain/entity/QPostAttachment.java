package gendervs.gendervs1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostAttachment is a Querydsl query type for PostAttachment
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostAttachment extends EntityPathBase<PostAttachment> {

    private static final long serialVersionUID = -2073730609L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostAttachment postAttachment = new QPostAttachment("postAttachment");

    public final NumberPath<Long> attachmentId = createNumber("attachmentId", Long.class);

    public final EnumPath<gendervs.gendervs1.domain.enums.FileType> fileType = createEnum("fileType", gendervs.gendervs1.domain.enums.FileType.class);

    public final StringPath fileUrl = createString("fileUrl");

    public final QPost post;

    public final DateTimePath<java.time.LocalDateTime> uploadedAt = createDateTime("uploadedAt", java.time.LocalDateTime.class);

    public QPostAttachment(String variable) {
        this(PostAttachment.class, forVariable(variable), INITS);
    }

    public QPostAttachment(Path<? extends PostAttachment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostAttachment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostAttachment(PathMetadata metadata, PathInits inits) {
        this(PostAttachment.class, metadata, inits);
    }

    public QPostAttachment(Class<? extends PostAttachment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
    }

}

