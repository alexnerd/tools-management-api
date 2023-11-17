package tech.konso.toolsmanagement.modules.business.tools.tool.service.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.konso.toolsmanagement.modules.business.tools.brand.service.BrandService;
import tech.konso.toolsmanagement.modules.business.tools.category.service.CategoryService;
import tech.konso.toolsmanagement.modules.business.tools.label.service.LabelService;
import tech.konso.toolsmanagement.modules.business.tools.tool.controller.dto.ToolRequest;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.Tool;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.enums.OwnershipType;

import java.util.UUID;

/**
 * Mapper for tool entity
 */
@Service
public class ToolsEntityMapper {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LabelService labelService;

    /**
     * Converts {@link ToolRequest} to {@link Tool} object.
     * <p>
     * Example:
     * <pre>
     *     toEntity(new Tool(), rq);
     * </pre>
     *
     * @param tool {@link Tool} object for save to database or update existing
     * @param rq {@link ToolRequest} object for converting to {@link Tool}
     * @return {@link Tool} saved object
     */
    public Tool toEntity(Tool tool, ToolRequest rq) {
        if (tool.getId() == null) {
            tool.setUuid(UUID.randomUUID());
        }
        tool.setName(rq.name());
        tool.setIsConsumable(rq.isConsumable());
        tool.setInventoryNumber(rq.inventoryNumber());
        tool.setResponsibleUuid(rq.responsibleUuid());
        tool.setProjectUuid(rq.projectUuid());
        tool.setPrice(rq.price());
        tool.setOwnershipType(OwnershipType.valueOf(rq.ownershipType()));
        tool.setRentTill(rq.rentTill());
        tool.setIsKit(rq.isKit());
        tool.setKitUuid(rq.kitUuid());
        tool.setPhotoUuid(rq.photoUuid());
        tool.setBrand(rq.brandId() == null ? null : brandService.getReference(rq.brandId()));
        tool.setCategory(rq.categoryId() == null ? null : categoryService.getReference(rq.categoryId()));

        tool.removeLabels();
        rq.labels().stream().map(labelId -> labelService.getReference(labelId)).forEach(tool::addLabel);

        tool.setIsArchived(rq.isArchived());
        return tool;
    }
}
