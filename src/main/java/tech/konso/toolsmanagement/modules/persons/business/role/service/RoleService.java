package tech.konso.toolsmanagement.modules.persons.business.role.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.persons.business.role.controller.dto.RoleRequest;
import tech.konso.toolsmanagement.modules.persons.business.role.persistence.dao.Role;
import tech.konso.toolsmanagement.modules.persons.business.role.persistence.repository.RoleRepository;
import tech.konso.toolsmanagement.modules.persons.business.role.persistence.specification.RoleSpecification;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;
import tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification;

import java.util.Optional;

import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

/**
 * Service layer for working with roles.
 */
@Service("PersonsRoleService")
public class RoleService {

    @Autowired
    private RoleRepository repository;

    /**
     * Find role in database by unique id. Role must exist in database
     * <p>
     * Example:
     * <pre>
     *     Role role = findById(2L);
     * </pre>
     *
     * @param id of role, must exist in database
     * @return role from database
     * @throws BPException if role not exists in database
     */
    public Role findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BPException("Role not found id: " + id));
    }

    /**
     * Get role reference by unique id. Used to link the role entity with other entities,
     * when the entire object from the database should not be loaded
     * <p>
     * Example:
     * <pre>
     *     Role role = getReferenceById(2L);
     * </pre>
     *
     * @param id of role, must exist in database
     * @return proxy role object
     */
    public Role getReference(Long id) {
        return repository.getReferenceById(id);
    }

    /**
     * Finds roles by role specification and returns it in pageable format.
     * By default, result set sorts by create date from newer to older and without archived roles.
     * <p>
     * Example:
     * <pre>
     *     Specification<Role> spec = specBuilder(sortSpec("name,desc")).build();
     *     Page<Role> foundedRoles = service.findAll(0, 100, spec);
     * </pre>
     *
     * @param page number of returned result set
     * @param size of the returned page
     * @param spec set of role specification
     * @return {@link Page<Role>} object for resulting dataset in pageable format
     * @see RoleSpecification role specifications
     */
    public Page<Role> findAll(int page, int size, Specification<Role> spec) {
        AbstractSpecification.SpecBuilder<Role> builder = specBuilder(Role.class);
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(builder.and(spec).build(), pageable);
    }

    /**
     * Save new role to database or update existing.
     * Role name must be unique and not exists in database.
     * <p>
     * Example:
     * <pre>
     *     RoleRequest rq = new RoleRequest(null, "new_role", false);
     *     Role savedRole = service.save(rq);
     * </pre>
     *
     * @param rq {@link RoleRequest} object for creating role
     * @return {@link Role} saved object
     */
    @Transactional
    public Role save(RoleRequest rq) {
        return Optional.ofNullable(rq.id())
                .map(id -> repository.findById(rq.id())
                        .orElseThrow(() -> new BPException("Role not found id: " + id))
                ).map(role -> toEntity(role, rq))
                .orElseGet(() ->
                        repository.save(toEntity(new Role(), rq))
                );
    }

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
    private Role toEntity(Role role, RoleRequest rq) {
        role.setName(rq.name());
        role.setIsArchived(rq.isArchived());
        return role;
    }
}
