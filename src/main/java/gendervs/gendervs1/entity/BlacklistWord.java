package gendervs.gendervs1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blacklist_word")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id")
    private Long wordId;

    @Column(name = "word", unique = true, length = 30)
    private String word;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

