package tech.konso.toolsmanagement.modules.tools.business.category.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tech.konso.toolsmanagement.modules.tools.business.category.persistence.dao.Category;

import java.util.List;

/**
 * DTO class for response to return result set of categories in pageable format.
 *
 * @param categories    list of found categories. List size limited by page size
 * @param totalItems    total number of categories found
 */

@Schema(description = "Response for return result set of categories in pageable format")
public record CategoryFilterResponse(@Schema(description = "list of found categories, list size limited by page size")
                                     List<Category> categories,
                                     @Schema(description = "total number of categories found", example = "20")
                                     Long totalItems) {
}
