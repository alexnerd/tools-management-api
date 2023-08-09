package tech.konso.toolsmanagement.modules.tools.business.category.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.tools.business.category.controller.dto.CategoryInfo;
import tech.konso.toolsmanagement.modules.tools.business.category.controller.dto.CategoryRequest;
import tech.konso.toolsmanagement.modules.tools.business.category.persistence.dao.Category;
import tech.konso.toolsmanagement.modules.tools.business.category.persistence.repository.CategoryRepository;
import tech.konso.toolsmanagement.modules.tools.business.category.persistence.specification.CategorySpecification;
import tech.konso.toolsmanagement.modules.tools.business.category.service.mappers.CategoryDtoMapper;
import tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;

import java.util.Optional;

import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

/**
 * Service layer for working with categories.
 */
@Slf4j
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    private CategoryDtoMapper mapper;

    @PostConstruct
    public void init() {
        mapper = new CategoryDtoMapper();
    }

    /**
     * Find category in database by unique id. Category must exist in database
     * <p>
     * Example:
     * <pre>
     *     CategoryInfo category = findById(2L);
     * </pre>
     *
     * @param id of category, must exist in database
     * @return {@link CategoryInfo} dto object mapped by category object from database
     * @throws BPException if category not exists in database
     */
    public CategoryInfo findById(Long id) {
        return repository.findById(id)
                .map(category -> mapper.mapToCategoryInfo(category))
                .orElseThrow(() -> new BPException("Category not found id: " + id));
    }

    /**
     * Get category reference by unique id. Used to link the category entity with other entities,
     * when the entire object from the database should not be loaded
     * <p>
     * Example:
     * <pre>
     *     Category category = getReferenceById(2L);
     * </pre>
     *
     * @param id of category, must exist in database
     * @return proxy category object
     */
    public Category getReference(Long id) {
        return repository.getReferenceById(id);
    }

    /**
     * Finds categories by category specification and returns it in pageable format.
     * By default, result set sorts by create date from newer to older and without archived categories.
     * <p>
     * Example:
     * <pre>
     *     Specification&lt;Category> spec = specBuilder(sortSpec("name,desc")).build();
     *     Page<Category> foundedCategorys = service.findAll(0, 100, spec);
     * </pre>
     *
     * @param page number of returned result set
     * @param size of the returned page
     * @param spec set of category specification
     * @return {@link Page<Category>} object for resulting dataset in pageable format
     * @see CategorySpecification category specifications
     */
    public Page<CategoryInfo> findAll(int page, int size, Specification<Category> spec) {
        AbstractSpecification.SpecBuilder<Category> builder = specBuilder(Category.class);
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(builder.and(spec).build(), pageable)
                .map(category -> mapper.mapToCategoryInfo(category));
    }

    /**
     * Save new category to database or update existing.
     * Category name must be unique and not exists in database.
     * If category to update become archived then it's subcategories become archived too.
     * Category to update must exist in database.
     * Category id and parent category id must not be the same.
     * Run under transaction.
     * <p>
     * Example:
     * <pre>
     *     CategoryRequest rq = new CategoryRequest("new_category", 1, false);
     *     Category savedCategory = service.save(rq);
     * </pre>
     *
     * @param rq {@link CategoryRequest} object for creating category
     * @return {@link Category} saved object
     */
    @Transactional
    public Category save(CategoryRequest rq) {
        return Optional.ofNullable(rq.id())
                .map(id -> repository.findById(rq.id())
                        .orElseThrow(() -> new BPException("Category not found id: " + id))
                ).map(category -> {
                    if (category.getId().equals(rq.parentCategoryId())) {
                        throw new BPException("Category id and parent category id must not be the same, id: "
                                + rq.parentCategoryId());
                    }
                    return toEntity(category, rq);
                })
                .orElseGet(() ->
                        repository.save(toEntity(new Category(), rq))
                );
    }

    /**
     * Converts {@link CategoryRequest} to {@link Category} object.
     * <p>
     * Example:
     * <pre>
     *     toEntity(new Category(), rq);
     * </pre>
     *
     * @param category {@link Category} object for save to database or update existing
     * @param rq       {@link CategoryRequest} object for converting to {@link Category}
     * @return {@link Category} saved object
     */
    private Category toEntity(Category category, CategoryRequest rq) {
        category.setName(rq.name());
        category.setParentCategory(
                rq.parentCategoryId() == null ? null : repository.getReferenceById(rq.parentCategoryId())
        );
        if (rq.isArchived() && !category.getIsArchived()) {
            category.getSubcategories().forEach(child -> child.setIsArchived(true));
        }
        category.setIsArchived(rq.isArchived());
        return category;
    }
}