package tech.konso.toolsmanagement.modules.business.tools.category.controller.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CategoryInfo(Long id,
                           String name,
                           Long parentCategoryId,
                           List<CategoryShort> subcategories,
                           Boolean isArchived,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
}
