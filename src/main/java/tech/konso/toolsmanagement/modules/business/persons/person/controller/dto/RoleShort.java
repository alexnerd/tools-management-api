package tech.konso.toolsmanagement.modules.business.persons.person.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Short role description DTO class for {@link PersonInfo}
 * used in response to describe person role by basic fields
 *
 * @param id     role id
 * @param name   role name
 */

@Builder
@Schema(description = "Short role description used to describe person role by basic fields")
public record RoleShort(@Schema(description = "role id", example = "1")
                         Long id,
                         @Schema(description = "role name", example = "Admin")
                         String name) {
}
