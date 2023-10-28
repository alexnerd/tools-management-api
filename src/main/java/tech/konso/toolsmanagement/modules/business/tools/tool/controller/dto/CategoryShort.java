package tech.konso.toolsmanagement.modules.business.tools.tool.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Short category description DTO class for {@link ToolInfo}
 * used in response to describe tool category by basic fields
 *
 * @param id     category id
 * @param name   category name
 */

@Builder
@Schema(description = "Short category description used to describe tool category by basic fields")
public record CategoryShort(@Schema(description = "category id", example = "1")
                            Long id,
                            @Schema(description = "category name", example = "Hand tool")
                            String name) {
}
