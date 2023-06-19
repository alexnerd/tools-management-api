package tech.konso.toolsmanagement.modules.tools.business.category.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto.ToolInfo;

/**
 * Short category description DTO class for {@link CategoryInfo}
 * used in response to describe sub categories by basic fields
 *
 * @param id            category id
 * @param name          category name
 * @param isArchived    archived category flag
 */

@Builder
@Schema(description = "Short category description used to describe sub category by basic fields")
public record CategoryShort(@Schema(description = "category id", example = "1")
                            Long id,
                            @Schema(description = "category name", example = "Hand tool")
                            String name,
                            @Schema(description = "archived flag", example = "false")
                            Boolean isArchived) {
}

