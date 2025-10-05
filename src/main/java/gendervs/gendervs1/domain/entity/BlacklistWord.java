package gendervs.gendervs1.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blacklist_words")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wordId;

    @Column(unique = true, length = 30)
    private String word;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

