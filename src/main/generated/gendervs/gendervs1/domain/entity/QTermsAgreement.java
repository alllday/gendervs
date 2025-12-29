package gendervs.gendervs1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTermsAgreement is a Querydsl query type for TermsAgreement
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTermsAgreement extends EntityPathBase<TermsAgreement> {

    private static final long serialVersionUID = 228314159L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTermsAgreement termsAgreement = new QTermsAgreement("termsAgreement");

    public final DateTimePath<java.time.LocalDateTime> agreedAt = createDateTime("agreedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> agreementId = createNumber("agreementId", Long.class);

    public final EnumPath<gendervs.gendervs1.domain.enums.TermsType> termsType = createEnum("termsType", gendervs.gendervs1.domain.enums.TermsType.class);

    public final QUser user;

    public QTermsAgreement(String variable) {
        this(TermsAgreement.class, forVariable(variable), INITS);
    }

    public QTermsAgreement(Path<? extends TermsAgreement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTermsAgreement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTermsAgreement(PathMetadata metadata, PathInits inits) {
        this(TermsAgreement.class, metadata, inits);
    }

    public QTermsAgreement(Class<? extends TermsAgreement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

