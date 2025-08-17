package gendervs.gendervs1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "topic_position")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long positionId;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "position_code", nullable = false, length = 1)
    private String positionCode;

    @Column(name = "position_text", nullable = false, length = 20)
    private String positionText;
}

