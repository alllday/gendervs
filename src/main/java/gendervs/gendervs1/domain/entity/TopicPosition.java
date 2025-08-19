package gendervs.gendervs1.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "topic_positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long positionId;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(nullable = false, length = 1)
    private String positionCode;

    @Column(nullable = false, length = 20)
    private String positionText;
}

