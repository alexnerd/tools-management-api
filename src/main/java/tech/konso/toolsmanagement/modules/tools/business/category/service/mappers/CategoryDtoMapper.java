package tech.konso.toolsmanagement.modules.tools.business.category.service.mappers;

import tech.konso.toolsmanagement.modules.tools.business.category.controller.dto.CategoryInfo;
import tech.konso.toolsmanagement.modules.tools.business.category.controller.dto.CategoryShort;
import tech.konso.toolsmanagement.modules.tools.business.category.persistence.dao.Category;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class for mapping {@link Category} objects to DTO
 */
public class CategoryDtoMapper {

    /**
     * Map full info from {@link Category} object to DTO
     *
     * @param category object to map
     * @return created dto object
     */
    public CategoryInfo mapToCategoryInfo(Category category) {
        return CategoryInfo.builder()
                .id(category.getId())
                .name(category.getName())
                .parentCategoryId(Optional.ofNullable(category.getParentCategory())
                        .map(Category::getId)
                        .orElse(null))
                .subcategories(category.getSubcategories().stream()
                        .map(this::mapToCategoryShort)
                        .collect(Collectors.toList()))
                .isArchived(category.getIsArchived())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    /**
     * Map basic info from {@link Category} object to DTO
     *
     * @param category object to map
     * @return created dto object
     */
    public CategoryShort mapToCategoryShort(Category category) {
        return CategoryShort.builder()
                .id(category.getId())
                .name(category.getName())
                .isArchived(category.getIsArchived())
                .build();
    }
}
