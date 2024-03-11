package tech.konso.toolsmanagement.modules.business.persons.role.service.mappers;

import tech.konso.toolsmanagement.modules.business.persons.role.controller.dto.RoleRequest;
import tech.konso.toolsmanagement.modules.business.persons.role.persistence.dao.Role;

/**
 * Mapper for role entity
 */

public class RoleEntityMapper {

    /**
     * Converts {@link RoleRequest} to {@link Role} object.
     * <p>
     * Example:
     * <pre>
     *     toEntity(new Role(), rq);
     * </pre>
     *
     * @param role {@link Role} object for save to database or update existing
     * @param rq {@link RoleRequest} object for converting to {@link Role}
     * @return {@link Role} saved object
     */
    public Role toEntity(Role role, RoleRequest rq) {
        role.setName(rq.name());
        role.setIsArchived(rq.isArchived());
        return role;
    }
}
