package tech.konso.toolsmanagement.modules.tools.business.category.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * DTO class for request to save new category or updating existing category.
 *
 * @param name                  of the category, must not be blank
 * @param parentCategoryId      id of parent category
 * @param isArchived            flag, must not be null
 */

@Builder
@Schema(description = "Request to save new category or updating existing category")
public record CategoryRequest(@Schema(description = "category name", example = "Hand tools")
                              @NotBlank String name,
                              @Schema(description = "parent category id", example = "Sub tools")
                              Long parentCategoryId,
                              @Schema(description = "archived flag", example = "false")
                              @NotNull Boolean isArchived) {
}
