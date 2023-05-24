package tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO class for response to return result set of tools in pageable format.
 *
 * @param tools      list of found tools. List size limited by page size
 * @param totalItems total number of tools found
 */

@Schema(description = "Response for return result set of tools in pageable format")
public record ToolFilterResponse(@Schema(description = "list of found tools, list size limited by page size")
                                 List<ToolFilterInfo> tools,
                                 @Schema(description = "total number of tools found", example = "20")
                                 Long totalItems) {
}
