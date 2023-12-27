package tech.konso.toolsmanagement.modules.business.tools.comment.persistence.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.Tool;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Comment entity class.
 * Relies on "tools_comment" table in database
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "tools_comment")
public class Comment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    /**
     * Comment content. Must be not nullable
     */
    @Column(nullable = false)
    private String content;

    /**
     * Business key from Module Persons - Person person
     */
    @Setter
    @Column(name = "person_uuid")
    private UUID personUuid;

    /**
     * Commented tool
     * Foreign key - id of tools_tool table
     */
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_id")
    private Tool tool;

    /**
     * Creation record date with time. By default, sets to current
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Update record date. By default, sets to current
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
