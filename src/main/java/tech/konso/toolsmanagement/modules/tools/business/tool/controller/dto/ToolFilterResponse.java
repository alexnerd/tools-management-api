package tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto;

import java.util.List;

/**
 * DTO class for response to return result set of tools in pageable format.
 *
 * @param tools      list of found tools. List size limited by page size
 * @param totalItems total number of tools found
 */
public record ToolFilterResponse(List<ToolFilterInfo> tools, Long totalItems) {
}
