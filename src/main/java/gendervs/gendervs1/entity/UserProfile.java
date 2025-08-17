package gendervs.gendervs1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "nickname", nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(name = "score")
    private Integer score = 0;

    @Column(name = "gender", nullable = false, length = 1)
    private String gender;

    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

