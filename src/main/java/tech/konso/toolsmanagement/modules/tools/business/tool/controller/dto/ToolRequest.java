package tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.dao.enums.OwnershipType;
import tech.konso.toolsmanagement.modules.tools.commons.validators.NullOrNotBlank;
import tech.konso.toolsmanagement.modules.tools.commons.validators.ValueOfEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * DTO class for request to save new tool or updating existing tool.
 *
 * @param name            of the tool, must not be blank
 * @param isConsumable    consumable mark, must not be null
 * @param inventoryNumber tool inventory number, may be null or must not be blank
 * @param responsibleUuid business key from Module Persons - Person person
 * @param projectUuid     business key from Module Projects - Project project"
 * @param price           tool price
 * @param ownershipType   type of ownership
 * @param rentTill        last day of tool rent
 * @param isKit           flag is the tool a kit, must not be null
 * @param kitUuid         kit uuid
 * @param brandId         identifier of the tool brand
 * @param categoryId      identifier of the category brand
 * @param labels          set of labels ids, must not be null
 * @param isArchived      flag, must not be null
 */

@Builder
public record ToolRequest(@Schema(description = "tool name", example = "Makita MTK24")
                          @NotBlank String name,
                          @Schema(description = "consumable mark", example = "false")
                          @NotNull Boolean isConsumable,
                          @Schema(description = "tool inventory number", example = "0014-HANDTOOL")
                          @NullOrNotBlank String inventoryNumber,
                          @Schema(description = "business key from Module Persons - Person person", example = "935921a7-692e-4ee4-a089-2695b68e9804")
                          UUID responsibleUuid,
                          @Schema(description = "business key from Module Projects - Project project", example = "3d965e4e-cf28-45e1-91c7-1225566e6811")
                          UUID projectUuid,
                          @Schema(description = "tool price", example = "23400.11")
                          BigDecimal price,
                          @Schema(description = "type of ownership", example = "OWN")
                          @NotNull(message = "ownershipType must not be null")
                          @ValueOfEnum(enumClass = OwnershipType.class)
                          String ownershipType,
                          @Schema(description = "last day of tool rent", example = "2024-12-30")
                          LocalDate rentTill,
                          @Schema(description = "flag is the tool a kit", example = "false")
                          @NotNull Boolean isKit,
                          @Schema(description = "kit uuid", example = "3996a6ee-c40e-4401-b8c8-2f96f49d0c22")
                          UUID kitUuid,
                          @Schema(description = "brand id", example = "21")
                          Long brandId,
                          @Schema(description = "category id", example = "17")
                          Long categoryId,
                          @Schema(description = "label ids", example = "[19, 38]")
                          @NotNull Set<Long> labels,
                          @Schema(description = "archived flag", example = "false")
                          @NotNull Boolean isArchived) {
}
