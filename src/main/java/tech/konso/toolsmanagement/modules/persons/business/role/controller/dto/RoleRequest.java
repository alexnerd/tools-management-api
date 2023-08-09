package tech.konso.toolsmanagement.modules.persons.business.role.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * DTO class for request to save new role or updating existing role.
 *
 * @param id         of role
 * @param name       of the role, must not be blank
 * @param isArchived flag, must not be null
 */

@Schema(description = "Request to save new role or updating existing role")
@Builder
public record RoleRequest(
        @Schema(description = "role id, if role is null then new role will be saved, " +
                "if id is not null, then existing role will update", example = "4") Long id,
        @Schema(description = "role name", example = "Attention") @NotBlank String name,
        @Schema(description = "archived flag", example = "false") @NotNull Boolean isArchived) {
}


