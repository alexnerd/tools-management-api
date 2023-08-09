package tech.konso.toolsmanagement.modules.persons.business.role.persistence.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Role entity class.
 * Relies on "persons_role" table in database
 */
@Getter
@Setter
@Entity(name = "PersonsRole")
@Table(name = "persons_role")
public class Role implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    /**
     * Role name. Must be unique and not nullable
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
        return id != null && id.equals(((Role) obj).id);
    }

    @Override
    public int hashCode() {
        return 2023;
    }
}
