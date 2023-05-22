package tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.dao.enums.OwnershipType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Tool info DTO class for get info by id API
 *
 * @param id              tool id
 * @param uuid            tool business key
 * @param name            of the tool, must not be blank
 * @param isConsumable    consumable flag, must not be null
 * @param brand           brand short description
 * @param inventoryNumber tool inventory number, may be null or must not be blank
 * @param responsible     responsible person name
 * @param category        category short description
 * @param project         project name
 * @param price           tool price
 * @param ownershipType   type of ownership
 * @param rentTill        last day of tool rent
 * @param isKit           flag is the tool a kit, must not be null
 * @param kitUuid         kit id
 * @param labels          set of labels with short description, must not be null
 * @param isArchived      flag, must not be null
 * @param createdAt       create date
 * @param updatedAt       update date
 */

@Builder
public record ToolInfo(@Schema(description = "tool id", example = "1")
                       Long id,
                       @Schema(description = "tool business key", example = "3996a6ee-c40e-4401-b8c8-2f96f49d0c22")
                       UUID uuid,
                       @Schema(description = "tool name", example = "Makita MTK24")
                       String name,
                       @Schema(description = "consumable flag", example = "true")
                       Boolean isConsumable,
                       @Schema(description = "brand short description", example = "{id: 1, name:\"Bosh\"}", nullable = true)
                       BrandShort brand,
                       @Schema(description = "tool inventory number", example = "0014-HANDTOOL", nullable = true)
                       String inventoryNumber,
                       @Schema(description = "responsible person name", example = "Jim Morrison", nullable = true)
                       String responsible,
                       @Schema(description = "category short description", example = "{id: 1, name:\"Handtool\"}", nullable = true)
                       CategoryShort category,
                       @Schema(description = "project name", example = "Some project", nullable = true)
                       String project,
                       @Schema(description = "tool price", example = "23400.11", nullable = true)
                       BigDecimal price,
                       @Schema(description = "type of ownership", example = "OWN")
                       OwnershipType ownershipType,
                       @Schema(description = "last day of tool rent", example = "2024-12-30", nullable = true)
                       LocalDate rentTill,
                       @Schema(description = "flag is the tool a kit", example = "false")
                       Boolean isKit,
                       @Schema(description = "kit uuid", example = "3996a6ee-c40e-4401-b8c8-2f96f49d0c22", nullable = true)
                       UUID kitUuid,
                       @Schema(description = "labels short descriptions", example = "[{id: 1, name:\"Attention\"},{id: 2, name:\"Important\"}]")
                       Set<LabelShort> labels,
                       @Schema(description = "archived flag", example = "false")
                       Boolean isArchived,
                       @Schema(description = "create date", example = "false")
                       LocalDateTime createdAt,
                       @Schema(description = "update date", example = "false")
                       LocalDateTime updatedAt) {
}