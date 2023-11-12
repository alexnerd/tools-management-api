package tech.konso.toolsmanagement.modules.business.tools.tool.service.mappers;

import tech.konso.toolsmanagement.modules.business.tools.brand.persistence.dao.Brand;
import tech.konso.toolsmanagement.modules.business.tools.category.persistence.dao.Category;
import tech.konso.toolsmanagement.modules.business.tools.label.persistence.dao.Label;
import tech.konso.toolsmanagement.modules.business.tools.tool.controller.dto.*;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.Tool;

import java.util.Optional;
import java.util.stream.Collectors;

public class ToolsDtoMapper {
    public ToolFilterInfo mapToToolFilterInfo(Tool tool) {
        return ToolFilterInfo.builder()
                .id(tool.getId())
                .uuid(tool.getUuid())
                .name(tool.getName())
                .isConsumable(tool.getIsConsumable())
                .brand(Optional.ofNullable(tool.getBrand()).map(Brand::getName).orElse(null))
                .inventoryNumber(tool.getInventoryNumber())
                //TODO: change after adding persons module
                .responsible(null)
                .category(Optional.ofNullable(tool.getCategory()).map(Category::getName).orElse(null))
                //TODO: change after adding projects module
                .project(null)
                .price(tool.getPrice())
                .ownershipType(tool.getOwnershipType())
                .rentTill(tool.getRentTill())
                .isKit(tool.getIsKit())
                .kitUuid(tool.getKitUuid())
                .labels(tool.getLabels().stream().map(Label::getName).collect(Collectors.toSet()))
                .isArchived(tool.getIsArchived())
                .createdAt(tool.getCreatedAt())
                .updatedAt(tool.getUpdatedAt())
                .build();
    }

    public ToolInfo mapToToolInfo(Tool tool) {
        return ToolInfo.builder()
                .id(tool.getId())
                .uuid(tool.getUuid())
                .name(tool.getName())
                .isConsumable(tool.getIsConsumable())
                .brand(Optional.ofNullable(tool.getBrand()).map(this::convertBrand).orElse(null))
                .inventoryNumber(tool.getInventoryNumber())
                //TODO: change after adding persons module
                .responsible(null)
                .category(Optional.ofNullable(tool.getCategory()).map(this::convertCategory).orElse(null))
                //TODO: change after adding projects module
                .project(null)
                .price(tool.getPrice())
                .ownershipType(tool.getOwnershipType())
                .rentTill(tool.getRentTill())
                .isKit(tool.getIsKit())
                .kitUuid(tool.getKitUuid())
                .labels(tool.getLabels().stream().map(this::convertLabel).collect(Collectors.toSet()))
                .isArchived(tool.getIsArchived())
                .createdAt(tool.getCreatedAt())
                .updatedAt(tool.getUpdatedAt())
                .build();
    }

    private BrandShort convertBrand(Brand brand) {
        return BrandShort.builder()
                .id(brand.getId())
                .name(brand.getName())
                .build();
    }

    private CategoryShort convertCategory(Category category) {
        return CategoryShort.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    private LabelShort convertLabel(Label label) {
        return LabelShort.builder()
                .id(label.getId())
                .name(label.getName())
                .build();
    }
}
