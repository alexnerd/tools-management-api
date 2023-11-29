package tech.konso.toolsmanagement.modules.business.tools.brand.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.business.tools.brand.persistence.dao.Brand;

/**
 * Repository for brand entity.
 * <p>Supports CRUD operations, searching by specification and pageable format.
 * <p>By default, uses read only transactions, if you want another mode, you must directly specify it on your method.
 */
@Repository
@Transactional(readOnly = true)
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Page<Brand> findAll(@Nullable Specification<Brand> spec, Pageable pageable);
}