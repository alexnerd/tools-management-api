package tech.konso.toolsmanagement.modules.business.persons.role.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import tech.konso.toolsmanagement.modules.business.persons.role.controller.dto.RoleFilterResponse;
import tech.konso.toolsmanagement.modules.business.persons.role.controller.dto.RoleRequest;
import tech.konso.toolsmanagement.modules.business.persons.role.persistence.dao.Role;
import tech.konso.toolsmanagement.modules.business.persons.role.service.RoleService;
import tech.konso.toolsmanagement.modules.business.persons.commons.AbstractControllerTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Role controller layer tests.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */
public class RoleControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;


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

    private String urlEndpoint() {
        return url + "/v1/persons/roles";
    }

    private RoleRequest.RoleRequestBuilder getDefaultRoleRequest() {
        return RoleRequest.builder()
                .name("ADMIN")
                .isArchived(false);
    }

    @Nested
    class FindTests {
        /**
         * {@link RoleController#find(Long)} should return {@link Role} by id from database.
         * Test checks status code 200 and equality roleId (received from jdbcTemplate request)
         * with id of role object received from {@link RoleService#findById(Long)} and name.
         */
        @Test
        public void find_should_return_role_test() throws Exception {
            long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = 'role_1' AND is_archived IS FALSE", Long.class);

            mockMvc.perform(get(urlEndpoint() + "/" + roleId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            Role.class,
                            dto -> dto.getId() == roleId && dto.getName().equals("role_1")
                    )));
        }

        /**
         * {@link RoleController#find(Long)} should return not found if {@link Role} with id not exist in database.
         * Test try to find role whit id = -1 (negative number guaranties, that no such id exists in database)
         * and check if controller return not found with detailed error message in header.
         */
        @Test
        public void find_should_return_not_found_test() throws Exception {
            long roleId = -1L;

            mockMvc.perform(get(urlEndpoint() + "/" + roleId))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(header().stringValues("detail", "Role not found id: " + roleId));
        }
    }

    @Nested
    class FindAllTests {
        /**
         * {@link RoleController#findAll(int, int, String, Boolean, String)}
         * should return bad request with incorrect page number. Test try to search all roles whit page = 0
         * and check if controller return bad request with detailed error message in header.
         */
        @Test
        public void findAll_with_incorrect_page_should_return_bab_request_test() throws Exception {
            String tail = "?page=0&size=20";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(header().stringValues("detail", "findAll.page: must be greater than or equal to 1"));
        }

        /**
         * {@link RoleController#findAll(int, int, String, Boolean, String)}
         * should return bad request with incorrect size number. Test try to search all roles whit size = 99999
         * and check if controller return bad request with detailed error message in header.
         */
        @Test
        public void findAll_with_incorrect_size_should_return_bab_request_test() throws Exception {
            String tail = "?page=1&size=99999";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isBadRequest()).
                    andExpect(header().stringValues("detail", "findAll.size: must be less than or equal to 50"));
        }

        /**
         * {@link RoleController#findAll(int, int, String, Boolean, String)}  without filters should return all not archived roles.
         * Test counts all not archived roles from the database.
         * Then test make request to find all roles and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_return_roles_without_filters_test() throws Exception {
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_role WHERE is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            RoleFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link RoleController#findAll(int, int, String, Boolean, String)}  with archived filter,
         * should return all archived roles.
         * Test counts all archived roles from the database.
         * Then test make request to find all roles and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_filter_archived_roles_test() throws Exception {
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_role WHERE is_archived IS TRUE", Long.class);
            String tail = "?page=1&size=20&isArchived=true";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            RoleFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link RoleController#findAll(int, int, String, Boolean, String)}  with not archived filter,
         * should return all not archived roles.
         * Test counts all not archived roles from the database.
         * Then test make request to find all roles and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_filter_not_archived_roles_test() throws Exception {
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_role WHERE is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20&isArchived=false";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            RoleFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link RoleController#findAll(int, int, String, Boolean, String)}  with filter by full name,
         * should return role with this name.
         * Test counts all role with concrete name.
         * Then test make request to find all roles and checks if it returns the same number, as plane jdbc request,
         * and checks if returns name equals with predefined name.
         */
        @Test
        public void findAll_should_filter_full_name_roles_test() throws Exception {
            String roleName = "role_1";
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_role WHERE name = '" + roleName + "' AND is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20&name=" + roleName;

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            RoleFilterResponse.class,
                            dto -> dto.totalItems() == count && dto.roles().get(0).getName().equals(roleName))
                    ));
        }

        /**
         * {@link RoleController#findAll(int, int, String, Boolean, String)}  with filter by part of name,
         * should return role with this name.
         * Test counts all role with like filter by name.
         * Then test make request to find all roles and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_filter_like_name_roles_test() throws Exception {
            jdbcTemplate.update("INSERT INTO persons_role (name) VALUES ('USER')");
            jdbcTemplate.update("INSERT INTO persons_role (name) VALUES ('DDLABEDDD')");
            String roleName = "lab";
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_role WHERE LOWER (name) LIKE '%" + roleName + "%' AND is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20&name=" + roleName;

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            RoleFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link RoleController#findAll(int, int, String, Boolean, String)}  with filter sorting by name asc,
         * should return sorted roles by names asc.
         * Test returns role names from database(using jdbcTemplate) ordered by name asc.
         * Then test make request to find all roles and checks if it returns the roles in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_name_asc_roles_test() throws Exception {
            List<String> roleNames = jdbcTemplate.queryForList("SELECT name FROM persons_role WHERE is_archived IS FALSE ORDER BY name ASC", String.class);
            String tail = "?page=1&size=20&sort=name,asc";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            RoleFilterResponse.class,
                            dto -> {
                                List<String> roleNamesResponse = dto.roles().stream().map(Role::getName).toList();
                                assertIterableEquals(roleNames, roleNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link RoleController#findAll(int, int, String, Boolean, String)}  with filter sorting by name desc,
         * should return sorted roles by name desc.
         * Test returns role names from database(using jdbcTemplate) ordered by name desc.
         * Then test make request to find all roles and checks if it returns the roles in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_name_desc_roles_test() throws Exception {
            List<String> roleNames = jdbcTemplate.queryForList("SELECT name FROM persons_role WHERE is_archived IS FALSE ORDER BY name DESC", String.class);
            String tail = "?page=1&size=20&sort=name,desc";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            RoleFilterResponse.class,
                            dto -> {
                                List<String> roleNamesResponse = dto.roles().stream().map(Role::getName).toList();
                                assertIterableEquals(roleNames, roleNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link RoleController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date asc,
         * should return sorted roles by create date asc.
         * Test returns role names from database(using jdbcTemplate) ordered by created date asc.
         * Then test make request to find all roles and checks if it returns the roles in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_create_date_asc_roles_test() throws Exception {
            List<String> roleNames = jdbcTemplate.queryForList("SELECT name FROM persons_role WHERE is_archived IS FALSE ORDER BY created_at ASC", String.class);
            String tail = "?page=1&size=20&sort=createdAt,asc";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            RoleFilterResponse.class,
                            dto -> {
                                List<String> roleNamesResponse = dto.roles().stream().map(Role::getName).toList();
                                assertIterableEquals(roleNames, roleNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link RoleController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date desc,
         * should return sorted roles by create date desc.
         * Test returns role names from database(using jdbcTemplate) ordered by created date desc.
         * Then test make request to find all roles and checks if it returns the roles in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_create_date_desc_roles_test() throws Exception {
            List<String> roleNames = jdbcTemplate.queryForList("SELECT name FROM persons_role WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);

            mockMvc.perform(get(urlEndpoint() + "?page=1&size=20&sort=createdAt,desc"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            RoleFilterResponse.class,
                            dto -> {
                                List<String> roleNamesResponse = dto.roles().stream().map(Role::getName).toList();
                                assertIterableEquals(roleNames, roleNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link RoleController#findAll(int, int, String, Boolean, String)}  without filter by default
         * should return sorted roles by create date desc.
         * Test returns role names from database(using jdbcTemplate) ordered by created date desc.
         * Then test make request to find all roles and checks if it returns the roles in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_create_date_desc_roles_by_default_test() throws Exception {
            List<String> roleNames = jdbcTemplate.queryForList("SELECT name FROM persons_role WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);
            String tail = "?page=1&size=20";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            RoleFilterResponse.class,
                            dto -> {
                                List<String> roleNamesResponse = dto.roles().stream().map(Role::getName).toList();
                                assertIterableEquals(roleNames, roleNamesResponse);
                                return true;
                            })
                    ));
        }
    }

    @Nested
    class UpdateTests {
        /**
         * {@link RoleController#update(RoleRequest)}  should update {@link Role} name field.
         * Test finds existing role id in database with jdbcTemplate.
         * Then send request for update role name by id.
         * Then checks if name was updated or not (by compare {@link RoleRequest} name and roleName received from database).
         */
        @Test
        public void update_should_update_role_name_test() throws Exception {
            long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = 'role_1' AND is_archived IS FALSE", Long.class);
            RoleRequest rq = getDefaultRoleRequest()
                    .id(roleId)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            String newRoleName = jdbcTemplate.queryForObject("SELECT name FROM persons_role WHERE role_id = " + roleId + " AND is_archived IS FALSE", String.class);
            assertEquals(rq.name(), newRoleName);
        }

        /**
         * {@link RoleController#update(RoleRequest)} should update {@link Role} isArchived flag.
         * Test finds existing role id in database with jdbcTemplate.
         * Then send request for update isArchived flag by id.
         * Then checks if isArchived was updated or not (by compare {@link RoleRequest} isArchived flag and flag received from database).
         */
        @Test
        public void update_should_update_role_is_archived_test() throws Exception {
            long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = 'role_1' AND is_archived IS FALSE", Long.class);
            RoleRequest rq = getDefaultRoleRequest()
                    .id(roleId)
                    .isArchived(true)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM persons_role WHERE role_id = " + roleId, Boolean.class);
            assertEquals(rq.isArchived(), isArchived);
        }

        /**
         * {@link RoleController#update(RoleRequest)} should return bad request with null role name.
         * Test finds existing role id in database with jdbcTemplate.
         * Then send request for update by id with null role name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_null_name_test() throws Exception {
            long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = 'role_1' AND is_archived IS FALSE", Long.class);
            RoleRequest rq = getDefaultRoleRequest()
                    .id(roleId)
                    .name(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link RoleController#update(RoleRequest)} should return bad request with blank role name.
         * Test finds existing role id in database with jdbcTemplate.
         * Then send request for update by id with blank role name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_blank_name_test() throws Exception {
            long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = 'role_1' AND is_archived IS FALSE", Long.class);
            RoleRequest rq = getDefaultRoleRequest()
                    .id(roleId)
                    .name("  ")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link RoleController#update(RoleRequest)} should return bad request with empty role name.
         * Test finds existing role id in database with jdbcTemplate.
         * Then send request for update by id with empty role name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_empty_name_test() throws Exception {
            long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = 'role_1' AND is_archived IS FALSE", Long.class);
            RoleRequest rq = getDefaultRoleRequest()
                    .id(roleId)
                    .name("")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link RoleController#update(RoleRequest)} should return bad request if role name already exists in database.
         * Test finds existing role name in database with jdbcTemplate.
         * Then finds another role by id with different in database with jdbcTemplate.
         * Then send request for update by id with existing role name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_existing_name_test() throws Exception {
            String existingRoleName = jdbcTemplate.queryForObject("SELECT name FROM persons_role WHERE name = 'role_1' AND is_archived IS FALSE", String.class);
            long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = 'role_2' AND is_archived IS FALSE", Long.class);
            RoleRequest rq = getDefaultRoleRequest()
                    .id(roleId)
                    .name(existingRoleName)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link RoleController#update(RoleRequest)} should return not found if role with searching id not exist in database.
         * Test send request for update by not existing id.
         * Then checks if controller response with not found.
         */
        @Test
        public void update_should_return_not_found_for_not_existing_id_test() throws Exception {
            RoleRequest rq = getDefaultRoleRequest()
                    .id(-1L)
                    .name("test")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class SaveTests {
        /**
         * {@link RoleController#save(RoleRequest)} should save {@link Role} object.
         * Test checks if roles with given name not exists in database.
         * Then sends request to create new role and checks status equals created.
         * Then receive number roles exists in database with new name.
         * Then cheks if number roles with new name equals one (new role saved to database).
         */
        @Test
        public void save_should_save_new_role_test() throws Exception {
            String roleName = "Important";
            Long countRoles = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_role WHERE name = '" + roleName + "' AND is_archived IS FALSE", Long.class);
            assertEquals(0, countRoles);

            RoleRequest rq = getDefaultRoleRequest()
                    .name(roleName)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isCreated());

            Long countSavedRoles = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_role WHERE name = '" + roleName + "' AND is_archived IS FALSE", Long.class);
            assertEquals(1L, countSavedRoles);
        }

        /**
         * {@link RoleController#save(RoleRequest)} should not save {@link Role} object if brnad already exists in database.
         * Test sends request to create new role with already existing role name in database.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_role_exists_test() throws Exception {
            RoleRequest rq = getDefaultRoleRequest()
                    .name("role_1")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link RoleController#save(RoleRequest)} should not save {@link Role} object if role name is null.
         * Test sends request to create new role with null role name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_name_null_test() throws Exception {
            RoleRequest rq = getDefaultRoleRequest()
                    .name(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link RoleController#save(RoleRequest)} should not save {@link Role} object if role name is empty.
         * Test sends request to create new role with empty role name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_name_empty_test() throws Exception {
            RoleRequest rq = getDefaultRoleRequest()
                    .name("")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link RoleController#save(RoleRequest)} should not save {@link Role} object if role name is blank.
         * Test sends request to create new role with blank role name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_name_blank_test() throws Exception {
            RoleRequest rq = getDefaultRoleRequest()
                    .name("   ")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link RoleController#save(RoleRequest)} should not save {@link Role} object if archived flag is null.
         * Test sends request to create new role with null archived flag.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_is_archived_null_test() throws Exception {
            RoleRequest rq = getDefaultRoleRequest()
                    .isArchived(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }
    }
}