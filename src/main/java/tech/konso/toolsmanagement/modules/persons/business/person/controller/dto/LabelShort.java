package tech.konso.toolsmanagement.modules.persons.business.person.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Short label description DTO class for {@link PersonInfo}
 * used in response to describe person label by basic fields
 *
 * @param id     label id
 * @param name   label name
 */

@Builder
@Schema(description = "Short label description used to describe person label by basic fields")
public record LabelShort(@Schema(description = "label id", example = "1")
                         Long id,
                         @Schema(description = "label name", example = "Attention")
                         String name) {
}

