package tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto;

import lombok.Builder;

/**
 * Short category description DTO class for {@link ToolInfo}
 * used in response to describe tool category by basic fields
 *
 * @param id     category id
 * @param name   category name
 */

@Builder
public record CategoryShort(Long id, String name) {
}
