package tech.konso.toolsmanagement.modules.tools.business.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.tools.business.category.controller.dto.CategoryRequest;
import tech.konso.toolsmanagement.modules.tools.business.category.persistence.dao.Category;
import tech.konso.toolsmanagement.modules.tools.business.category.persistence.repository.CategoryRepository;
import tech.konso.toolsmanagement.modules.tools.business.category.persistence.specification.CategorySpecification;
import tech.konso.toolsmanagement.modules.tools.commons.AbstractSpecification;
import tech.konso.toolsmanagement.modules.tools.commons.exceptions.BPException;

import static tech.konso.toolsmanagement.modules.tools.commons.AbstractSpecification.specBuilder;

/**
 * Service layer for working with categories.
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    /**
     * Find category in database by unique id. Category must exist in database
     * <p>
     * Example:
     * <pre>
     *     Category category = findById(2L);
     * </pre>
     *
     * @param id of category, must exist in database
     * @return category from database
     * @throws BPException if category not exists in database
     */
    public Category findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BPException("Category not found id: " + id));
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
    public Page<Category> findAll(int page, int size, Specification<Category> spec) {
        AbstractSpecification.SpecBuilder<Category> builder = specBuilder(Category.class);
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(builder.and(spec).build(), pageable);
    }

    /**
     * Update category by unique id. Supports updating name and archived flag.
     * Category to update must exist in database.
     * Run under transaction.
     * <p>
     * Example:
     * <pre>
     *     CategoryRequest rq = new CategoryRequest("new_category", true);
     *     service.update(categoryId, rq);
     * </pre>
     *
     * @param id of category, must exist in database
     * @param rq {@link CategoryRequest} object for updating category
     * @return {@link Category} updated category object
     * @throws BPException if category not exists in database
     */
    @Transactional
    public Category update(Long id, CategoryRequest rq) {
        Category category = repository.findById(id).orElseThrow(() -> new BPException("Category not found id: " + id));
        category.setName(rq.name());
        category.setIsArchived(rq.isArchived());
        return category;
    }

    /**
     * Save new category to database.
     * Category name must be unique and not exists in database.
     * <p>
     * Example:
     * <pre>
     *     CategoryRequest rq = new CategoryRequest("new_category", false);
     *     Category savedCategory = service.save(rq);
     * </pre>
     *
     * @param rq {@link CategoryRequest} object for creating category
     * @return {@link Category} saved object
     */
    public Category save(CategoryRequest rq) {
        Category category = new Category();
        category.setName(rq.name());
        category.setIsArchived(rq.isArchived());
        return repository.save(category);
    }
}