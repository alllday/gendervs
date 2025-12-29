package gendervs.gendervs1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QBlacklistWord is a Querydsl query type for BlacklistWord
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlacklistWord extends EntityPathBase<BlacklistWord> {

    private static final long serialVersionUID = 935494747L;

    public static final QBlacklistWord blacklistWord = new QBlacklistWord("blacklistWord");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath word = createString("word");

    public final NumberPath<Long> wordId = createNumber("wordId", Long.class);

    public QBlacklistWord(String variable) {
        super(BlacklistWord.class, forVariable(variable));
    }

    public QBlacklistWord(Path<? extends BlacklistWord> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBlacklistWord(PathMetadata metadata) {
        super(BlacklistWord.class, metadata);
    }

}

