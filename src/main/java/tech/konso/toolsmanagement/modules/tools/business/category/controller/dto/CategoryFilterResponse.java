package tech.konso.toolsmanagement.modules.tools.business.category.controller.dto;

import tech.konso.toolsmanagement.modules.tools.business.category.persistence.dao.Category;

import java.util.List;

/**
 * DTO class for response to return result set of categories in pageable format.
 *
 * @param categories    list of found categories. List size limited by page size
 * @param totalItems    total number of categories found
 */
public record CategoryFilterResponse(List<Category> categories, Long totalItems) {
}
