package tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO class for response to return result set of comments in pageable format.
 *
 * @param comments   list of found comments. List size limited by page size
 * @param totalItems total number of comments found
 */

@Schema(description = "Response for return result set of comments in pageable format")
public record CommentFilterResponse(@Schema(description = "list of found comments, list size limited by page size")
                                    List<CommentFilterInfo> comments,
                                    @Schema(description = "total number of comments found", example = "20")
                                    Long totalItems) {
}
