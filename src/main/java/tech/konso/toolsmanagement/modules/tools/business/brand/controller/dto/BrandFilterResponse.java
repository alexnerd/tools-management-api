package tech.konso.toolsmanagement.modules.tools.business.brand.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tech.konso.toolsmanagement.modules.tools.business.brand.persistence.dao.Brand;

import java.util.List;

/**
 * DTO class for response to return result set of brands in pageable format.
 *
 * @param brands     list of found brands. List size limited by page size
 * @param totalItems total number of brands found
 */

@Schema(description = "Response for return result set of brands in pageable format")
public record BrandFilterResponse(@Schema(description = "list of found brands, list size limited by page size")
                                  List<Brand> brands,
                                  @Schema(description = "total number of brands found", example = "20")
                                  Long totalItems) {
}
