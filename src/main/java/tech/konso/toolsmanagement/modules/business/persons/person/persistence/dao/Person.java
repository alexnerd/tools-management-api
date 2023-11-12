package tech.konso.toolsmanagement.modules.business.persons.person.persistence.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tech.konso.toolsmanagement.modules.business.persons.role.persistence.dao.Role;
import tech.konso.toolsmanagement.modules.business.persons.label.persistence.dao.Label;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Person entity class.
 * Relies on "persons_person" table in database
 */
@Getter
@Setter
@Entity
@Table(name = "persons_person")
public class Person implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long id;

    /**
     * Business key
     */
    @Setter
    @Column(nullable = false, updatable = false, unique = true)
    private UUID uuid;

    /**
     * Person phone number
     */
    @Setter
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Business key from Module Companies - Company company
     */
    @Setter
    @Column(name = "company_uuid")
    private UUID companyUuid;

    /**
     * Person surname
     */
    @Setter
    @Column(nullable = false)
    private String surname;

    /**
     * Person name
     */
    @Setter
    @Column(nullable = false)
    private String name;

    /**
     * Person patronymic
     */
    @Setter
    @Column
    private String patronymic;

    /**
     * Person job title
     */
    @Setter
    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    /**
     * Active flag. Must be not nullable
     */
    @Setter
    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived;

    /**
     * Unregistered flag. Must be not nullable
     */
    @Setter
    @Column(name = "is_unregistered", nullable = false)
    private Boolean isUnregistered;

    /**
     * Uuid of photo in file storage
     */
    @Setter
    @Column(name = "photo_uuid")
    private UUID photoUuid;

    /**
     * Person roles
     * Foreign keys - id of persons_persons_roles table
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "persons_person_role",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * Person labels
     * Foreign keys - id of persons_persons_roles table
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "persons_person_label",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id"))
    private Set<Label> labels = new HashSet<>();

    /**
     * Creation record date with time. By default, sets to current
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Update record date. By default, sets to current
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public void addLabel(Label label) {
        this.labels.add(label);
    }

    public void removeLabel(Label label) {
        this.labels.remove(label);
    }

    public void removeLabels() {
        this.labels.clear();
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void removeRoles() {
        this.roles.clear();
    }

}
