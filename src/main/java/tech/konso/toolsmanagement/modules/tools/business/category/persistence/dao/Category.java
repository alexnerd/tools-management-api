package tech.konso.toolsmanagement.modules.tools.business.category.persistence.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Category entity class.
 * Relies on "tools_category" table in database
 */
@Getter
@Setter
@Entity
@Table(name = "tools_category")
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    /**
     * Category name. Must be unique and not nullable
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Archived mark. Must be not nullable
     */
    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived;

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