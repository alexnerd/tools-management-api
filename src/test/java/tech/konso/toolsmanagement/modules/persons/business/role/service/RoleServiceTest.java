package tech.konso.toolsmanagement.modules.persons.business.role.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import tech.konso.toolsmanagement.PostgreSQLContainerExtension;
import tech.konso.toolsmanagement.modules.persons.business.role.controller.dto.RoleFilterResponse;
import tech.konso.toolsmanagement.modules.persons.business.role.controller.dto.RoleRequest;
import tech.konso.toolsmanagement.modules.persons.business.role.persistence.dao.Role;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tech.konso.toolsmanagement.modules.persons.business.role.persistence.specification.RoleSpecification.*;
import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

/**
 * Role service layer tests.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */
@SpringBootTest
@ExtendWith(PostgreSQLContainerExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RoleService service;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO persons_role (name) VALUES ('role_1')");
        jdbcTemplate.update("INSERT INTO persons_role (name) VALUES ('role_2')");
        jdbcTemplate.update("INSERT INTO persons_role (name) VALUES ('role_3')");
        jdbcTemplate.update("INSERT INTO persons_role (name) VALUES ('role_4')");
        jdbcTemplate.update("INSERT INTO persons_role (name) VALUES ('role_5')");
        jdbcTemplate.update("INSERT INTO persons_role (name, is_archived) VALUES ('role_6',  'true')");

    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM persons_role");
    }

    private RoleRequest.RoleRequestBuilder getDefaultRoleRequest() {
        return RoleRequest.builder()
                .name("ADMIN")
                .isArchived(false);
    }

    /**
     * {@link RoleService#findById(Long)} should return {@link Role} by id from database.
     * Test checks equality roleId (received from jdbcTemplate request)
     * with id of role object received from {@link RoleService#findById(Long)}
     */
    @Test
    public void findById_should_return_role_test() {
        long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = 'role_1' AND is_archived IS FALSE", Long.class);

        Role role = service.findById(roleId);

        assertEquals(roleId, role.getId());
    }

    /**
     * {@link RoleService#findById(Long)} should throw {@link BPException} exception
     * if {@link Role} with id not exist in database.
     * Test try to find role whit id = -1 (negative num,ber guaranties, that no such id exists in database)
     * and check if {@link BPException} is thrown.
     */
    @Test
    public void findById_should_throw_exception_on_not_fount_role_test() {
        long roleId = -1;

        assertThrows(BPException.class, () -> service.findById(roleId));
    }

    /**
     * {@link RoleService#save(RoleRequest)} should update {@link Role} name field.
     * Test finds existing role id in database with jdbcTemplate and try to update it name
     * using {@link RoleService#save(RoleRequest)}.
     * Then checks if name was updated or not (by compare {@link RoleRequest} name and roleName received from database).
     */
    @Test
    public void update_should_update_role_name_test() {
        long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = 'role_1' AND is_archived IS FALSE", Long.class);
        RoleRequest rq = getDefaultRoleRequest()
                .id(roleId)
                .build();

        service.save(rq);

        String roleName = jdbcTemplate.queryForObject("SELECT name FROM persons_role WHERE role_id = " + roleId + " AND is_archived IS FALSE", String.class);
        assertEquals(rq.name(), roleName);
    }

    /**
     * {@link RoleService#save(RoleRequest)} should update {@link Role} isArchived field.
     * Test finds existing role id in database with jdbcTemplate and try to update it isArchived flag
     * using {@link RoleService#save(RoleRequest)}.
     * Then checks if isArchived flag was updated or not (using assertTrue on field).
     */
    @Test
    public void update_should_archive_role_test() {
        long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = 'role_1' AND is_archived IS FALSE", Long.class);
        RoleRequest rq = getDefaultRoleRequest()
                .id(roleId)
                .isArchived(true)
                .build();

        service.save(rq);

        Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM persons_role WHERE role_id = " + roleId, Boolean.class);
        assertTrue(isArchived);
    }

    /**
     * {@link RoleService#save(RoleRequest)} should not update {@link Role} if name field is null.
     * Test finds existing role id in database with jdbcTemplate and try to update it name field
     * using {@link RoleService#save(RoleRequest)}.
     * Then checks if exception {@link DataIntegrityViolationException} was thrown.
     * Then checks if field name not changed during test.
     */
    @Test
    public void update_should_not_update_null_name_test() {
        String roleName = "role_1";
        long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = '" + roleName + "' AND is_archived IS FALSE", Long.class);
        RoleRequest rq = getDefaultRoleRequest()
                .id(roleId)
                .name(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        String roleNameFromDb = jdbcTemplate.queryForObject("SELECT name FROM persons_role WHERE role_id = " + roleId + " AND is_archived IS FALSE", String.class);
        assertEquals(roleName, roleNameFromDb);
    }

    /**
     * {@link RoleService#save(RoleRequest)} should not update {@link Role} if isArchived flag is null.
     * Test finds existing role id in database with jdbcTemplate and try to update it isArchived flag
     * using {@link RoleService#save(RoleRequest)}.
     * Then checks if exception {@link DataIntegrityViolationException} was thrown.
     * Then checks if isArchived flag not changed during test.
     */
    @Test
    public void update_should_not_update_null_isArchived_test() {
        long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = 'role_1' AND is_archived IS FALSE", Long.class);
        RoleRequest rq = getDefaultRoleRequest()
                .id(roleId)
                .isArchived(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM persons_role WHERE role_id = " + roleId, Boolean.class);
        assertFalse(isArchived);
    }

    /**
     * {@link RoleService#save(RoleRequest)}} should save {@link Role} object.
     * Test creates dto object {@link RoleRequest} and then using {@link RoleService#save(RoleRequest)}
     * try to save new {@link Role} object to database.
     * Then checks returns {@link Role} object if id not null, name field and isArchived flag equals this fields
     * from dto object {@link RoleRequest}.
     */
    @Test
    public void save_should_save_role_test() {
        RoleRequest rq = getDefaultRoleRequest().build();

        Role savedRole = service.save(rq);

        assertNotNull(savedRole.getId());
        assertEquals(rq.name(), savedRole.getName());
        assertEquals(rq.isArchived(), savedRole.getIsArchived());
    }

    /**
     * {@link RoleService#save(RoleRequest)}} should not save {@link Role} object
     * if field name already exists in database.
     * Test try to save Role with existing field name and check if {@link DataIntegrityViolationException} is thrown.
     * Then check if only one role with given name exist in database.
     */
    @Test
    public void save_should_not_save_if_role_name_already_exists_test() {
        String roleName = "role_1";
        RoleRequest rq = getDefaultRoleRequest()
                .name(roleName)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_role WHERE name = '" + roleName + "'", Long.class);
        assertEquals(1L, count);
    }

    /**
     * {@link RoleService#save(RoleRequest)}} should not save {@link Role} object if field name is null.
     * Test try to save Role with null field name and check if {@link DataIntegrityViolationException} is thrown.
     * Then test checks if there is no roles with null name exists in the database
     */
    @Test
    public void save_should_not_save_if_role_name_is_null_exists_test() {
        RoleRequest rq = getDefaultRoleRequest()
                .name(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_role WHERE name IS NULL", Long.class);
        assertEquals(0L, count);
    }

    /**
     * {@link RoleService#findAll(int, int, Specification)} should return all not archived {@link Role} objects.
     * Test counts all not archived role objects in the database using jdbcTemplate
     * Then test build isArchived specification for not archived roles and try to find them with {@link RoleService#findAll(int, int, Specification)}
     * Then test checks if counts not archived roles from jdbcTemplate equals totalItems from {@link RoleFilterResponse}
     * received from {@link RoleService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_all_not_archived_roles_test() {
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_role WHERE is_archived IS FALSE", Long.class);
        Specification<Role> spec = specBuilder(isArchivedSpec(false)).build();

        Page<Role> foundedRoles = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedRoles.getTotalElements());
    }

    /**
     * {@link RoleService#findAll(int, int, Specification)} should return all archived {@link Role} objects.
     * Test counts all archived role objects in the database using jdbcTemplate
     * Then test build isArchived specification for archived roles and try to find them with {@link RoleService#findAll(int, int, Specification)}
     * Then test checks if counts archived roles from jdbcTemplate equals totalItems from {@link RoleFilterResponse}
     * received from {@link RoleService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_all_archived_roles_test() {
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_role WHERE is_archived IS TRUE", Long.class);
        Specification<Role> spec = specBuilder(isArchivedSpec(true)).build();

        Page<Role> foundedRoles = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedRoles.getTotalElements());
    }

    /**
     * {@link RoleService#findAll(int, int, Specification)} should return {@link Role} objects by like name pattern.
     * Test counts all role objects in the database matches %ran% pattern using jdbcTemplate
     * Then test build like specification for name with %ran% pattern and try to find them with {@link RoleService#findAll(int, int, Specification)}
     * Then test checks if counts roles from jdbcTemplate equals totalItems from {@link RoleFilterResponse}
     * received from {@link RoleService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_by_name_roles_test() {
        String likeName = "lab";
        jdbcTemplate.update("INSERT INTO persons_role (name) VALUES ('USER')");
        jdbcTemplate.update("INSERT INTO persons_role (name) VALUES ('DDLABEDDD')");
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_role WHERE LOWER (name) LIKE '%" + likeName + "%'", Long.class);
        Specification<Role> spec = specBuilder(likeSpec(likeName)).build();

        Page<Role> foundedRoles = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedRoles.getTotalElements());
    }

    /**
     * {@link RoleService#findAll(int, int, Specification)} should sort {@link Role} objects by name in asc order.
     * Test receives all role names from the database in asc order
     * Then test build sort specification for order roles by name in asc order and try to find them with {@link RoleService#findAll(int, int, Specification)}
     * Then test checks if order of role names received from jdbcTemplate equals order of role names from {@link RoleFilterResponse}
     * received from {@link RoleService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_asc_by_name_roles_test() {
        List<String> roleNames = jdbcTemplate.queryForList("SELECT name FROM persons_role ORDER BY name ASC", String.class);
        Specification<Role> spec = specBuilder(sortSpec("name,asc")).build();

        Page<Role> foundedRoles = service.findAll(0, 100, spec);

        List<String> resultRoleNames = foundedRoles.getContent().stream().map(Role::getName).toList();
        assertIterableEquals(roleNames, resultRoleNames);
    }

    /**
     * {@link RoleService#findAll(int, int, Specification)} should sort {@link Role} objects by name in desc order.
     * Test receives all role names from the database in desc order
     * Then test build sort specification for order roles by name in desc order and try to find them with {@link RoleService#findAll(int, int, Specification)}
     * Then test checks if order of role names received from jdbcTemplate equals order of role names from {@link RoleFilterResponse}
     * received from {@link RoleService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_desc_by_name_roles_test() {
        List<String> roleNames = jdbcTemplate.queryForList("SELECT name FROM persons_role ORDER BY name DESC", String.class);
        Specification<Role> spec = specBuilder(sortSpec("name,desc")).build();

        Page<Role> foundedRoles = service.findAll(0, 100, spec);

        List<String> resultRoleNames = foundedRoles.getContent().stream().map(Role::getName).toList();
        assertIterableEquals(roleNames, resultRoleNames);
    }

    /**
     * {@link RoleService#findAll(int, int, Specification)} should sort {@link Role} objects by createdAt in asc order.
     * Test receives all role names from the database ordered by createdAt in asc order
     * Then test build sort specification for order roles by createdAt in asc order and try to find them with {@link RoleService#findAll(int, int, Specification)}
     * Then test checks if order of role names received from jdbcTemplate equals order of role names from {@link RoleFilterResponse}
     * received from {@link RoleService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_asc_by_created_date_roles_test() {
        List<String> roleNames = jdbcTemplate.queryForList("SELECT name FROM persons_role ORDER BY created_at ASC", String.class);
        Specification<Role> spec = specBuilder(sortSpec("createdat,asc")).build();

        Page<Role> foundedRoles = service.findAll(0, 100, spec);

        List<String> resultRoleNames = foundedRoles.getContent().stream().map(Role::getName).toList();
        assertIterableEquals(roleNames, resultRoleNames);
    }

    /**
     * {@link RoleService#findAll(int, int, Specification)} should sort {@link Role} objects by createdAt in desc order.
     * Test receives all role names from the database ordered by createdAt in desc order
     * Then test build sort specification for order roles by createdAt in desc order and try to find them with {@link RoleService#findAll(int, int, Specification)}
     * Then test checks if order of role names received from jdbcTemplate equals order of role names from {@link RoleFilterResponse}
     * received from {@link RoleService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_desc_by_created_date_roles_test() {
        List<String> roleNames = jdbcTemplate.queryForList("SELECT name FROM persons_role ORDER BY created_at DESC", String.class);
        Specification<Role> spec = specBuilder(sortSpec("createdat,desc")).build();

        Page<Role> foundedRoles = service.findAll(0, 100, spec);

        List<String> resultRoleNames = foundedRoles.getContent().stream().map(Role::getName).toList();
        assertIterableEquals(roleNames, resultRoleNames);
    }

    /**
     * {@link RoleService#findAll(int, int, Specification)} without filters, by default should sort {@link Role} objects by createdAt in desc order.
     * Test receives all role names from the database ordered by createdAt in desc order
     * Then test build sort specification null parameter and try to find roles with {@link RoleService#findAll(int, int, Specification)}
     * Then test checks if order of role names received from jdbcTemplate equals order of role names from {@link RoleFilterResponse}
     * received from {@link RoleService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_null_filter_should_sort_by_default_desc_by_created_date_roles_test() {
        List<String> roleNames = jdbcTemplate.queryForList("SELECT name FROM persons_role ORDER BY created_at DESC", String.class);
        Specification<Role> spec = specBuilder(sortSpec(null)).build();

        Page<Role> foundedRoles = service.findAll(0, 100, spec);

        List<String> resultRoleNames = foundedRoles.getContent().stream().map(Role::getName).toList();
        assertIterableEquals(roleNames, resultRoleNames);
    }

    /**
     * {@link RoleService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Role} objects by createdAt in desc order.
     * Test receives all role names from the database ordered by createdAt in desc order
     * Then test build sort specification with empty field "  " parameter and try to find roles with {@link RoleService#findAll(int, int, Specification)}
     * Then test checks if order of role names received from jdbcTemplate equals order of role names from {@link RoleFilterResponse}
     * received from {@link RoleService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_blank_filter_should_sort_by_default_desc_by_created_date_roles_test() {
        List<String> roleNames = jdbcTemplate.queryForList("SELECT name FROM persons_role ORDER BY created_at DESC", String.class);
        Specification<Role> spec = specBuilder(sortSpec(" ")).build();

        Page<Role> foundedRoles = service.findAll(0, 100, spec);

        List<String> resultRoleNames = foundedRoles.getContent().stream().map(Role::getName).toList();
        assertIterableEquals(roleNames, resultRoleNames);
    }

    /**
     * {@link RoleService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Role} objects by createdAt in desc order.
     * Test receives all role names from the database ordered by createdAt in desc order
     * Then test build sort specification with unsupported parameter and try to find roles with {@link RoleService#findAll(int, int, Specification)}
     * Then test checks if order of role names received from jdbcTemplate equals order of role names from {@link RoleFilterResponse}
     * received from {@link RoleService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_unsupported_filter_should_sort_by_default_desc_by_created_date_roles_test() {
        List<String> roleNames = jdbcTemplate.queryForList("SELECT name FROM persons_role ORDER BY created_at DESC", String.class);
        Specification<Role> spec = specBuilder(sortSpec("unsupported_filter")).build();

        Page<Role> foundedRoles = service.findAll(0, 100, spec);

        List<String> resultRoleNames = foundedRoles.getContent().stream().map(Role::getName).toList();
        assertIterableEquals(roleNames, resultRoleNames);
    }

    /**
     * {@link RoleService#findAll(int, int, Specification)} should return roles with specified size.
     * Test creates null specification.
     * Then test with page size 1 returns {@link RoleFilterResponse} from  {@link RoleService#findAll(int, int, Specification)}
     * Then test checks total received items and concrete size of founded roles in concrete page.
     */
    @Test
    public void findAll_with_page_size_one_should_return_one_role_test() {
        Specification<Role> spec = specBuilder(sortSpec(null)).build();

        Page<Role> foundedRoles = service.findAll(0, 1, spec);

        assertEquals(6, foundedRoles.getTotalElements());
        assertEquals(1, foundedRoles.getContent().size());
    }
}