package tech.konso.toolsmanagement.modules.business.tools.label.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * DTO class for request to save new label or updating existing label.
 *
 * @param id         of label
 * @param name       of the label, must not be blank
 * @param isArchived flag, must not be null
 */

@Schema(description = "Request to save new label or updating existing label")
@Builder
public record LabelRequest(
        @Schema(description = "label id, if label is null then new label will be saved, " +
                "if id is not null, then existing label will update", example = "4") Long id,
        @Schema(description = "label name", example = "Attention") @NotBlank String name,
        @Schema(description = "archived flag", example = "false") @NotNull Boolean isArchived) {
}
