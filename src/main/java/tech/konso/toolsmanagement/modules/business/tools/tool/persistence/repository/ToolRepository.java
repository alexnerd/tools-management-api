package tech.konso.toolsmanagement.modules.business.tools.tool.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.Tool;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for tool entity.
 * <p>Supports CRUD operations, searching by specification and pageable format.
 * <p>By default, uses read only transactions, if you want another mode, you must directly specify it on your method.
 */
@Repository
@Transactional(readOnly = true)
public interface ToolRepository extends JpaRepository<Tool, Long>, JpaSpecificationExecutor<Tool> {

    @Override
    @EntityGraph(attributePaths = {"labels", "brand", "category"},
            type = EntityGraph.EntityGraphType.FETCH)
    Optional<Tool> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"labels", "brand", "category"},
            type = EntityGraph.EntityGraphType.FETCH)
    Page<Tool> findAll(@Nullable Specification<Tool> spec, Pageable pageable);

    @Query("SELECT t.photoUuid FROM Tool t WHERE t.id = :id")
    Optional<UUID> findPhotoUuidByToolId(Long id);
}