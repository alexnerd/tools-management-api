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
 * Tool info DTO class for {@link ToolFilterResponse}
 * used in response for find tools by parameters
 *
 * @param id              tool id
 * @param uuid            tool business key
 * @param name            of the tool, must not be blank
 * @param isConsumable    consumable flag, must not be null
 * @param brand           brand name
 * @param inventoryNumber tool inventory number, may be null or must not be blank
 * @param responsible     responsible person name
 * @param category        category name
 * @param project         project name
 * @param price           tool price
 * @param ownershipType   type of ownership
 * @param rentTill        last day of tool rent
 * @param isKit           flag is the tool a kit, must not be null
 * @param kitUuid         kit id
 * @param labels          set of labels ids, must not be null
 * @param isArchived      flag, must not be null
 * @param createdAt       create date
 * @param updatedAt       update date
 */

@Builder
public record ToolFilterInfo(@Schema(description = "tool id", example = "1")
                             Long id,
                             @Schema(description = "tool business key", example = "3996a6ee-c40e-4401-b8c8-2f96f49d0c22")
                             UUID uuid,
                             @Schema(description = "tool name", example = "Makita MTK24")
                             String name,
                             @Schema(description = "consumable flag", example = "true")
                             Boolean isConsumable,
                             @Schema(description = "brand name", example = "Bosh", nullable = true)
                             String brand,
                             @Schema(description = "tool inventory number", example = "0014-HANDTOOL", nullable = true)
                             String inventoryNumber,
                             @Schema(description = "responsible person name", example = "Jim Morrison", nullable = true)
                             String responsible,
                             @Schema(description = "category name", example = "Handtool", nullable = true)
                             String category,
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
                             @Schema(description = "labels names", example = "[\"Attention\",\"Important\"]")
                             Set<String> labels,
                             @Schema(description = "archived flag", example = "false")
                             Boolean isArchived,
                             @Schema(description = "create date", example = "false")
                             LocalDateTime createdAt,
                             @Schema(description = "update date", example = "false")
                             LocalDateTime updatedAt) {
}