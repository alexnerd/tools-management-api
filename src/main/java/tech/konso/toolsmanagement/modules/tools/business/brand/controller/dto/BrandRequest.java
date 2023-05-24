package tech.konso.toolsmanagement.modules.tools.business.brand.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO class for request to save new brand or updating existing brand.
 *
 * @param name       of the brand, must not be blank
 * @param isArchived flag, must not be null
 */

@Schema(description = "Request to save new brand or updating existing brand")
public record BrandRequest(@Schema(description = "brand name", example = "Makita")
                           @NotBlank String name,
                           @Schema(description = "archived flag", example = "false")
                           @NotNull Boolean isArchived) {
}
