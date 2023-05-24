package tech.konso.toolsmanagement.modules.tools.business.label.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO class for request to save new label or updating existing label.
 *
 * @param name       of the label, must not be blank
 * @param isArchived flag, must not be null
 */

public record LabelRequest(@Schema(description = "label name", example = "Attention")
                           @NotBlank String name,
                           @Schema(description = "archived flag", example = "false")
                           @NotNull Boolean isArchived) {
}
