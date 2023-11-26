package tech.konso.toolsmanagement.modules.business.stocks.stock.persistence.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stock entity class.
 * Relies on "stocks_stock" table in database
 */
@Getter
@Setter
@Entity
@Table(name = "stocks_stock")
public class Stock {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    /**
     * Business key
     */
    @Setter
    @Column(nullable = false, updatable = false, unique = true)
    private UUID uuid;

    /**
     * Stock name
     */
    @Setter
    @Column(nullable = false)
    private String name;

    /**
     * Stock address
     */
    @Setter
    @Column(nullable = false)
    private String address;

    /**
     * Uuid of stock company
     */
    @Setter
    @Column(name = "company_uuid")
    private UUID companyUuid;

    /**
     * Uuid of stock responsible company
     */
    @Setter
    @Column(name = "responsible_company_uuid")
    private UUID responsibleCompanyUuid;

    /**
     * Uuid of stock responsible person
     */
    @Setter
    @Column(name = "responsible_person_uuid")
    private UUID responsiblePersonUuid;

    /**
     * Active flag. Must be not nullable
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
}
