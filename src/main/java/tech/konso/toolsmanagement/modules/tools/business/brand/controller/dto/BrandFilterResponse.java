package tech.konso.toolsmanagement.modules.tools.business.brand.controller.dto;

import tech.konso.toolsmanagement.modules.tools.business.brand.persistence.dao.Brand;

import java.util.List;

/**
 * DTO class for response to return result set of brands in pageable format.
 *
 * @param brands     list of found brands. List size limited by page size
 * @param totalItems total number of brands found
 */
public record BrandFilterResponse(List<Brand> brands, Long totalItems) {
}
