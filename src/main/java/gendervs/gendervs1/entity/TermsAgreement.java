package gendervs.gendervs1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "terms_agreement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TermsAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agreement_id")
    private Long agreementId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "terms_type", nullable = false, length = 20)
    private String termsType;

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;
}

