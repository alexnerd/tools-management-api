package tech.konso.toolsmanagement.modules.tools.business.tool.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.dao.Tool;

import java.util.Optional;

/**
 * Repository for tool entity.
 * <p>Supports CRUD operations, searching by specification and pageable format.
 * <p>By default, uses read only transactions, if you want another mode, you must directly specify it on your method.
 */
@Repository
@Transactional(readOnly = true)
public interface ToolRepository extends CrudRepository<Tool, Long>, JpaSpecificationExecutor<Tool> {

    @Override
    @EntityGraph(attributePaths = {"labels", "brand", "category"},
            type = EntityGraph.EntityGraphType.FETCH)
    Optional<Tool> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"labels", "brand", "category"},
                 type = EntityGraph.EntityGraphType.FETCH)
    Page<Tool> findAll(@Nullable Specification<Tool> spec, Pageable pageable);
}