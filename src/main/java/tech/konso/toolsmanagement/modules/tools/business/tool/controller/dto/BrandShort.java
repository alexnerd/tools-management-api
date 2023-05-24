package tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto;

import lombok.Builder;

/**
 * Short brand description DTO class for {@link ToolInfo}
 * used in response to describe tool brand by basic fields
 *
 * @param id     brand id
 * @param name   brand name
 */

@Builder
public record BrandShort(Long id, String name) {
}
