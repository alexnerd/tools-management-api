package tech.konso.toolsmanagement.modules.tools.business.category.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO class for request to save new category or updating existing category.
 *
 * @param name       of the category, must not be blank
 * @param isArchived flag, must not be null
 */
public record CategoryRequest(@Schema(description = "category name", example = "Hand tools")
                              @NotBlank String name,
                              @Schema(description = "archived flag", example = "false")
                              @NotNull Boolean isArchived) {
}
