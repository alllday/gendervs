package gendervs.gendervs1.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private Long userId; // real PK

    @OneToOne
    @MapsId // PK와 FK를 같은 컬럼으로 매핑
    @JoinColumn(name = "user_id")
    private User user; // 연관관계

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    private int score = 0;

    @Column(nullable = false, length = 1)
    private String gender;

    @Column(nullable = false)
    private LocalDate birth;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

