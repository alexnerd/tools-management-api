package tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Short brand description DTO class for {@link ToolInfo}
 * used in response to describe tool brand by basic fields
 *
 * @param id     brand id
 * @param name   brand name
 */

@Builder
@Schema(description = "Short brand description used to describe tool brand by basic fields")
public record BrandShort(@Schema(description = "brand id", example = "1")
                         Long id,
                         @Schema(description = "brand name", example = "Bosh")
                         String name) {
}
