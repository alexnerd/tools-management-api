package tech.konso.toolsmanagement.modules.tools.business.label.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tech.konso.toolsmanagement.modules.tools.business.label.persistence.dao.Label;

import java.util.List;

/**
 * DTO class for response to return result set of labels in pageable format.
 *
 * @param labels     list of found labels. List size limited by page size
 * @param totalItems total number of labels found
 */

@Schema(description = "Response for return result set of labels in pageable format")
public record LabelFilterResponse(@Schema(description = "list of found labels, list size limited by page size")
                                  List<Label> labels,
                                  @Schema(description = "total number of labels found")
                                  Long totalItems) {
}
