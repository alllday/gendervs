package gendervs.gendervs1.domain.entity;

import gendervs.gendervs1.domain.enums.TermsType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "terms_agreements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TermsAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agreementId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TermsType termsType;

    @CreationTimestamp
    private LocalDateTime agreedAt;
}

