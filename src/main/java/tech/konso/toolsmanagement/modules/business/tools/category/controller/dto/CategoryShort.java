package tech.konso.toolsmanagement.modules.business.tools.category.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

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

