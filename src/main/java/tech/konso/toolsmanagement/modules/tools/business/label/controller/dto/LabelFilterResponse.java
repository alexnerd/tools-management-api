package tech.konso.toolsmanagement.modules.tools.business.label.controller.dto;

import tech.konso.toolsmanagement.modules.tools.business.label.persistence.dao.Label;

import java.util.List;

/**
 * DTO class for response to return result set of labels in pageable format.
 *
 * @param labels     list of found labels. List size limited by page size
 * @param totalItems total number of labels found
 */
public record LabelFilterResponse(List<Label> labels, Long totalItems) {
}
