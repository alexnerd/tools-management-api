package tech.konso.toolsmanagement.modules.business.tools.category.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.business.tools.category.persistence.dao.Category;

import java.util.Optional;

/**
 * Repository for category entity.
 * <p>Supports CRUD operations, searching by specification and pageable format.
 * <p>By default, uses read only transactions, if you want another mode, you must directly specify it on your method.
 */
@Repository
@Transactional(readOnly = true)
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    @Override
    @EntityGraph(attributePaths = {"subcategories"},
            type = EntityGraph.EntityGraphType.FETCH)
    Optional<Category> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"subcategories"},
            type = EntityGraph.EntityGraphType.FETCH)
    Page<Category> findAll(@Nullable Specification<Category> spec, Pageable pageable);
}