package tech.konso.toolsmanagement.modules.tools.business.tool.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.tools.business.brand.service.BrandService;
import tech.konso.toolsmanagement.modules.tools.business.category.service.CategoryService;
import tech.konso.toolsmanagement.modules.tools.business.label.service.LabelService;
import tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto.ToolFilterInfo;
import tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto.ToolFilterResponse;
import tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto.ToolInfo;
import tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto.ToolRequest;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.dao.Tool;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.dao.enums.OwnershipType;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.repository.ToolRepository;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.specification.ToolSpecification;
import tech.konso.toolsmanagement.modules.tools.business.tool.service.mappers.ToolsDtoMapper;
import tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;

import java.util.Optional;
import java.util.UUID;

import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

/**
 * Service layer for working with tools.
 */
@Service
public class ToolService {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private ToolRepository repository;

    private ToolsDtoMapper toolsDtoMapper;

    @PostConstruct
    public void init() {
        toolsDtoMapper = new ToolsDtoMapper();
    }

    /**
     * Find tool in database by unique id. Tool must exist in database
     * <p>
     * Example:
     * <pre>
     *     Tool tool = findById(2L);
     * </pre>
     *
     * @param id of tool, must exist in database
     * @return tool from database
     * @throws BPException if tool not exists in database
     */
    public ToolInfo findById(Long id) {
        return repository.findById(id).map(toolsDtoMapper::mapToToolInfo).orElseThrow(() -> new BPException("Tool not found id: " + id));
    }

    /**
     * Finds tools by tool specification and returns it in pageable format.
     * By default, result set sorts by create date from newer to older and without archived tools.
     * <p>
     * Example:
     * <pre>
     *     Specification&lt;Tool> spec = specBuilder(sortSpec("name,desc")).build();
     *     ToolFilterResponse foundedTools = service.findAll(0, 100, spec);
     * </pre>
     *
     * @param page number of returned result set
     * @param size of the returned page
     * @param spec set of tool specification
     * @return {@link ToolFilterResponse} object for resulting dataset in pageable format
     * @see ToolSpecification tool specifications
     */
    public Page<ToolFilterInfo> findAll(int page, int size, Specification<Tool> spec) {
        AbstractSpecification.SpecBuilder<Tool> builder = specBuilder(Tool.class);
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(builder.and(spec).build(), pageable).map(toolsDtoMapper::mapToToolFilterInfo);
    }

    /**
     * Save new tool to database or update existing.
     * Run under transaction.
     * <p>
     * Example:
     * <pre>
     *     ToolRequest rq = new ToolRequest(null, "new_tool", null, null, false);
     *     Tool savedTool = service.save(rq);
     * </pre>
     *
     * @param rq {@link ToolRequest} object for creating tool
     * @return {@link Tool} saved object
     */
    @Transactional
    public Tool save(ToolRequest rq) {
        return Optional.ofNullable(rq.id())
                .map(id -> repository.findById(rq.id())
                        .orElseThrow(() -> new BPException("Tool not found id: " + id))
                ).map(tool -> toEntity(tool, rq))
                .orElseGet(() ->
                        repository.save(toEntity(new Tool(), rq))
                );
    }

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
    private Tool toEntity(Tool tool, ToolRequest rq) {
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
        tool.setBrand(rq.brandId() == null ? null : brandService.getReference(rq.brandId()));
        tool.setCategory(rq.categoryId() == null ? null : categoryService.getReference(rq.categoryId()));

        tool.removeLabels();
        rq.labels().stream().map(labelId -> labelService.getReference(labelId)).forEach(tool::addLabel);

        tool.setIsArchived(rq.isArchived());
        return tool;
    }
}