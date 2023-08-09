package tech.konso.toolsmanagement.modules.persons.business.role.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.persons.business.role.persistence.dao.Role;

/**
 * Repository for role entity.
 * <p>Supports CRUD operations, searching by specification and pageable format.
 * <p>By default, uses read only transactions, if you want another mode, you must directly specify it on your method.
 */
@Repository("PersonsRoleRepository")
@Transactional(readOnly = true)
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
}
