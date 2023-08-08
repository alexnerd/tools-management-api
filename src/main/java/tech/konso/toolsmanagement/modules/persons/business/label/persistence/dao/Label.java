package tech.konso.toolsmanagement.modules.persons.business.label.persistence.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Label entity class.
 * Relies on "persons_label" table in database
 */
@Getter
@Setter
@Entity(name = "PersonsLabel")
@Table(name = "persons_label")
public class Label implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "label_id")
    private Long id;

    /**
     * Label name. Must be unique and not nullable
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Archived flag. Must be not nullable
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


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return id != null && id.equals(((tech.konso.toolsmanagement.modules.persons.business.label.persistence.dao.Label) obj).id);
    }

    @Override
    public int hashCode() {
        return 2023;
    }
}