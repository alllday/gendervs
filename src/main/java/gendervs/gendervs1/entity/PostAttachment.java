package gendervs.gendervs1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_attachment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long attachmentId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "file_url", nullable = false, length = 255)
    private String fileUrl;

    @Column(name = "file_type", nullable = false, length = 10)
    private String fileType;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
}

