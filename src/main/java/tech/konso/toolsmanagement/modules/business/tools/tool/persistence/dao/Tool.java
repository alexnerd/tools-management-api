package tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tech.konso.toolsmanagement.modules.business.tools.brand.persistence.dao.Brand;
import tech.konso.toolsmanagement.modules.business.tools.category.persistence.dao.Category;
import tech.konso.toolsmanagement.modules.business.tools.label.persistence.dao.Label;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.enums.OwnershipType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Tool entity class.
 * Relies on "tools_tool" table in database
 */
@Getter
@Setter
@Entity
@Table(name = "tools_tool")
public class Tool implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tool_id")
    private Long id;

    /**
     * Business key
     */
    @Setter
    @Column(nullable = false, updatable = false, unique = true)
    private UUID uuid;

    /**
     * Tool name. Must be unique and not nullable
     */
    @Setter
    @Column(nullable = false)
    private String name;

    /**
     * Consumable flag. Must be not nullable (расходный материал)
     */
    @Setter
    @Column(name = "is_consumable", nullable = false)
    private Boolean isConsumable;

    /**
     * Tool inventory number
     */
    @Setter
    @Column(name = "inventory_number")
    private String inventoryNumber;

    /**
     * Business key from Module Persons - Person person
     */
    @Setter
    @Column(name = "responsible_uuid")
    private UUID responsibleUuid;

    /**
     * Business key from Module Projects - Project project
     */
    @Setter
    @Column(name = "project_uuid")
    private UUID projectUuid;

    /**
     * Tool price
     */
    @Setter
    @Column(name = "price")
    private BigDecimal price;

    /**
     * Type of ownership
     */
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "ownership_type", nullable = false)
    private OwnershipType ownershipType;

    /**
     * last day of tool rent
     */
    @Setter
    @Column(name = "rent_till")
    private LocalDate rentTill;

    /**
     * Flag is the tool a kit
     */
    @Setter
    @Column(name = "is_kit", nullable = false)
    private Boolean isKit;

    /**
     * Kit uuid
     */
    @Setter
    @Column(name = "kit_uuid")
    private UUID kitUuid;

    /**
     * Uuid of photo in file storage
     */
    @Setter
    @Column(name = "photo_uuid")
    private UUID photoUuid;

    /**
     * Tool brand
     * Foreign key - id of tools_brand table
     */
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    /**
     * Tool category
     * Foreign key - id of tools_category table
     */
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Tool labels
     * Foreign keys - id of tools_tools_label table
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "tools_tool_label",
            joinColumns = @JoinColumn(name = "tool_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id"))
    private Set<Label> labels = new HashSet<>();

    /**
     * Archived flag. Must be not nullable
     */
    @Setter
    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived;

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

}