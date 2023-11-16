package tech.konso.toolsmanagement.modules.business.tools.tool.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.enums.OwnershipType;
import tech.konso.toolsmanagement.system.commons.validators.NullOrNotBlank;
import tech.konso.toolsmanagement.system.commons.validators.ValueOfEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * DTO class for request to save new tool or updating existing tool.
 *
 * @param id              of tool, may be null
 * @param name            of the tool, must not be blank
 * @param isConsumable    consumable flag, must not be null
 * @param inventoryNumber tool inventory number, may be null or must not be blank
 * @param responsibleUuid business key from Module Persons - Person person
 * @param projectUuid     business key from Module Projects - Project project"
 * @param price           tool price
 * @param ownershipType   type of ownership
 * @param rentTill        last day of tool rent
 * @param isKit           flag is the tool a kit, must not be null
 * @param kitUuid         kit uuid
 * @param photoUuid       photo uuid
 * @param brandId         identifier of the tool brand
 * @param categoryId      identifier of the category brand
 * @param labels          set of labels ids, must not be null
 * @param isArchived      flag, must not be null
 */

@Builder
@Schema(description = "Request to save new tool or updating existing tool")
public record ToolRequest(
        @Schema(description = "tool id, if tool is null then new tool will be saved, " +
                "if id is not null, then existing tool will update", example = "4")
        Long id,
        @Schema(description = "tool name", example = "Makita MTK24")
        @NotBlank String name,
        @Schema(description = "consumable flag", example = "false")
        @NotNull Boolean isConsumable,
        @Schema(description = "tool inventory number", example = "0014-HANDTOOL", nullable = true)
        @NullOrNotBlank
        String inventoryNumber,
        @Schema(description = "business key from Module Persons - Person person", example = "935921a7-692e-4ee4-a089-2695b68e9804", nullable = true)
        UUID responsibleUuid,
        @Schema(description = "business key from Module Projects - Project project", example = "3d965e4e-cf28-45e1-91c7-1225566e6811", nullable = true)
        UUID projectUuid,
        @Schema(description = "tool price", example = "23400.11", nullable = true)
        BigDecimal price,
        @Schema(description = "type of ownership", example = "OWN")
        @NotNull(message = "ownershipType must not be null")
        @ValueOfEnum(enumClass = OwnershipType.class)
        String ownershipType,
        @Schema(description = "last day of tool rent", example = "2024-12-30", nullable = true)
        LocalDate rentTill,
        @Schema(description = "flag is the tool a kit", example = "false")
        @NotNull
        Boolean isKit,
        @Schema(description = "kit uuid", example = "3996a6ee-c40e-4401-b8c8-2f96f49d0c22", nullable = true)
        UUID kitUuid,
        @Schema(description = "photo uuid", example = "eee7339f-4977-4186-bb5a-3a73585efe65", nullable = true)
        UUID photoUuid,
        @Schema(description = "brand id", example = "21", nullable = true)
        Long brandId,
        @Schema(description = "category id", example = "17", nullable = true)
        Long categoryId,
        @Schema(description = "label ids", example = "[19, 38]")
        @NotNull
        Set<Long> labels,
        @Schema(description = "archived flag", example = "false")
        @NotNull
        Boolean isArchived) {
}
