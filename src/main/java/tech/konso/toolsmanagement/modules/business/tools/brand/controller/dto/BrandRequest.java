package tech.konso.toolsmanagement.modules.business.tools.brand.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * DTO class for request to save new brand or updating existing brand.
 *
 * @param id         of brand
 * @param name       of the brand, must not be blank
 * @param isArchived flag, must not be null
 */

@Schema(description = "Request to save new brand or updating existing brand")
@Builder
public record BrandRequest(
        @Schema(description = "brand id, if brand is null then new brand will be saved, " +
                "if id is not null, then existing brand will update", example = "4") Long id,
        @Schema(description = "brand name", example = "Makita") @NotBlank String name,
        @Schema(description = "archived flag", example = "false") @NotNull Boolean isArchived) {
}
