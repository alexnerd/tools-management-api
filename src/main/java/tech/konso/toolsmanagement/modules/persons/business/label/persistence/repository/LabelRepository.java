package tech.konso.toolsmanagement.modules.persons.business.label.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.persons.business.label.persistence.dao.Label;

/**
 * Repository for label entity.
 * <p>Supports CRUD operations, searching by specification and pageable format.
 * <p>By default, uses read only transactions, if you want another mode, you must directly specify it on your method.
 */
@Repository("PersonsLabelRepository")
@Transactional(readOnly = true)
public interface LabelRepository extends JpaRepository<Label, Long>, JpaSpecificationExecutor<Label> {
}
