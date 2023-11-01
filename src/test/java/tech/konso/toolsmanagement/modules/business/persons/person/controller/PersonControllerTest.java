package tech.konso.toolsmanagement.modules.business.persons.person.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import tech.konso.toolsmanagement.modules.business.persons.commons.AbstractControllerTest;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.PersonFilterInfo;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.PersonFilterResponse;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.PersonRequest;
import tech.konso.toolsmanagement.modules.business.persons.person.persistence.dao.Person;
import tech.konso.toolsmanagement.modules.business.persons.person.service.PersonService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Person controller layer tests.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */
public class PersonControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String urlEndpoint() {
        return url + "/v1/persons/person";
    }

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid) VALUES ('surname_1', 'name_1', 'job_title_1', '935921a7-692e-4ee4-a089-2695b68e9801')");
        jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid) VALUES ('surname_2', 'name_2', 'job_title_2', '935921a7-692e-4ee4-a089-2695b68e9802')");
        jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid) VALUES ('surname_3', 'name_3', 'job_title_3', '935921a7-692e-4ee4-a089-2695b68e9803')");
        jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid) VALUES ('surname_4', 'name_4', 'job_title_4', '935921a7-692e-4ee4-a089-2695b68e9804')");
        jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid) VALUES ('surname_5', 'name_5', 'job_title_5', '935921a7-692e-4ee4-a089-2695b68e9805')");
        jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid, is_archived) VALUES ('surname_6', 'name_6', 'job_title_6', '935921a7-692e-4ee4-a089-2695b68e9806', 'true')");
    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM persons_person_label");
        jdbcTemplate.update("DELETE FROM persons_person_role");
        jdbcTemplate.update("DELETE FROM persons_person");
        jdbcTemplate.update("DELETE FROM persons_label");
        jdbcTemplate.update("DELETE FROM persons_role");

    }

    /**
     * Create {@link PersonRequest.PersonRequestBuilder} object with required non-null fields.
     */
    private PersonRequest.PersonRequestBuilder getDefaultPersonRequest() {
        return PersonRequest.builder()
                .surname("Surname")
                .name("Name")
                .jobTitle("Job title")
                .isArchived(false)
                .isUnregistered(false)
                .roles(Collections.emptySet())
                .labels(Collections.emptySet());
    }

    @Nested
    class FindTests {
        /**
         * {@link PersonController#find(Long)} should return {@link Person} by id from database.
         * Test checks status code 200 and equality personId (received from jdbcTemplate request)
         * with id of person object received from {@link PersonService#findById(Long)} and name.
         */
        @Test
        public void find_should_return_person_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);

            mockMvc.perform(get(urlEndpoint() + "/" + personId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            Person.class,
                            dto -> dto.getId() == personId && dto.getName().equals("name_1")
                    )));
        }

        /**
         * {@link PersonController#find(Long)} should return bad request if {@link Person} with id not exist in database.
         * Test try to find person whit id = -1 (negative number guaranties, that no such id exists in database)
         * and check if controller return bad request with detailed error message in header.
         */
        @Test
        public void find_should_return_bad_request_test() throws Exception {
            long personId = -1L;

            mockMvc.perform(get(urlEndpoint() + "/" + personId))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(header().stringValues("detail", "Person not found id: " + personId));
        }
    }

    @Nested
    class FindAllTests {
        /**
         * {@link PersonController#findAll(int, int, String, Boolean, String)}
         * should return bad request with incorrect page number. Test try to search all persons whit page = 0
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
         * {@link PersonController#findAll(int, int, String, Boolean, String)}
         * should return bad request with incorrect size number. Test try to search all persons whit size = 99999
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
         * {@link PersonController#findAll(int, int, String, Boolean, String)}  without filters should return all not archived persons.
         * Test counts all not archived persons from the database.
         * Then test make request to find all persons and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_return_persons_without_filters_test() throws Exception {
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_person WHERE is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            PersonFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link PersonController#findAll(int, int, String, Boolean, String)}  with archived filter,
         * should return all archived persons.
         * Test counts all archived persons from the database.
         * Then test make request to find all persons and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_filter_archived_persons_test() throws Exception {
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_person WHERE is_archived IS TRUE", Long.class);
            String tail = "?page=1&size=20&isArchived=true";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            PersonFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link PersonController#findAll(int, int, String, Boolean, String)}  with not archived filter,
         * should return all not archived persons.
         * Test counts all not archived persons from the database.
         * Then test make request to find all persons and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_filter_not_archived_persons_test() throws Exception {
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_person WHERE is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20&isArchived=false";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            PersonFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link PersonController#findAll(int, int, String, Boolean, String)}  with filter by full name,
         * should return person with this name.
         * Test counts all person with concrete name.
         * Then test make request to find all persons and checks if it returns the same number, as plane jdbc request,
         * and checks if returns name equals with predefined name.
         */
        @Test
        public void findAll_should_filter_full_name_persons_test() throws Exception {
            String personName = "name_1";
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_person WHERE name = '" + personName + "' AND is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20&name=" + personName;

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            PersonFilterResponse.class,
                            dto -> dto.totalItems() == count && dto.persons().get(0).name().equals(personName))
                    ));
        }

        /**
         * {@link PersonController#findAll(int, int, String, Boolean, String)}  with filter by part of name,
         * should return person with this name.
         * Test counts all person with like filter by name.
         * Then test make request to find all persons and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_filter_like_name_persons_test() throws Exception {
            jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid) VALUES ('Stone', 'Michele', 'manager', '935921a7-692e-4ee4-a089-2695b68e9891')");
            jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid) VALUES ('Shepard', 'Alex', 'manager', '935921a7-692e-4ee4-a089-2695b68e9892')");
            String personName = "pard";
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_person WHERE LOWER (name) LIKE '%" + personName + "%' AND is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20&name=" + personName;

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            PersonFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link PersonController#findAll(int, int, String, Boolean, String)}  with filter sorting by name asc,
         * should return sorted persons by names asc.
         * Test returns person names from database(using jdbcTemplate) ordered by name asc.
         * Then test make request to find all persons and checks if it returns the persons in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_name_asc_persons_test() throws Exception {
            List<String> personNames = jdbcTemplate.queryForList("SELECT name FROM persons_person WHERE is_archived IS FALSE ORDER BY name ASC", String.class);
            String tail = "?page=1&size=20&sort=name,asc";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            PersonFilterResponse.class,
                            dto -> {
                                List<String> personNamesResponse = dto.persons().stream().map(PersonFilterInfo::name).toList();
                                assertIterableEquals(personNames, personNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link PersonController#findAll(int, int, String, Boolean, String)}  with filter sorting by name desc,
         * should return sorted persons by name desc.
         * Test returns person names from database(using jdbcTemplate) ordered by name desc.
         * Then test make request to find all persons and checks if it returns the persons in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_name_desc_persons_test() throws Exception {
            List<String> personNames = jdbcTemplate.queryForList("SELECT name FROM persons_person WHERE is_archived IS FALSE ORDER BY name DESC", String.class);
            String tail = "?page=1&size=20&sort=name,desc";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            PersonFilterResponse.class,
                            dto -> {
                                List<String> personNamesResponse = dto.persons().stream().map(PersonFilterInfo::name).toList();
                                assertIterableEquals(personNames, personNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link PersonController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date asc,
         * should return sorted persons by create date asc.
         * Test returns person names from database(using jdbcTemplate) ordered by created date asc.
         * Then test make request to find all persons and checks if it returns the persons in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_create_date_asc_persons_test() throws Exception {
            List<String> personNames = jdbcTemplate.queryForList("SELECT name FROM persons_person WHERE is_archived IS FALSE ORDER BY created_at ASC", String.class);
            String tail = "?page=1&size=20&sort=createdAt,asc";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            PersonFilterResponse.class,
                            dto -> {
                                List<String> personNamesResponse = dto.persons().stream().map(PersonFilterInfo::name).toList();
                                assertIterableEquals(personNames, personNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link PersonController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date desc,
         * should return sorted persons by create date desc.
         * Test returns person names from database(using jdbcTemplate) ordered by created date desc.
         * Then test make request to find all persons and checks if it returns the persons in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_create_date_desc_persons_test() throws Exception {
            List<String> personNames = jdbcTemplate.queryForList("SELECT name FROM persons_person WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);

            mockMvc.perform(get(urlEndpoint() + "?page=1&size=20&sort=createdAt,desc"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            PersonFilterResponse.class,
                            dto -> {
                                List<String> personNamesResponse = dto.persons().stream().map(PersonFilterInfo::name).toList();
                                assertIterableEquals(personNames, personNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link PersonController#findAll(int, int, String, Boolean, String)}  without filter by default
         * should return sorted persons by create date desc.
         * Test returns person names from database(using jdbcTemplate) ordered by created date desc.
         * Then test make request to find all persons and checks if it returns the persons in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_create_date_desc_persons_by_default_test() throws Exception {
            List<String> personNames = jdbcTemplate.queryForList("SELECT name FROM persons_person WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);
            String tail = "?page=1&size=20";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            PersonFilterResponse.class,
                            dto -> {
                                List<String> personNamesResponse = dto.persons().stream().map(PersonFilterInfo::name).toList();
                                assertIterableEquals(personNames, personNamesResponse);
                                return true;
                            })
                    ));
        }
    }

    @Nested
    class UpdateTests {
        /**
         * {@link PersonController#update(PersonRequest)} should update {@link Person} isArchived flag.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update isArchived flag by id.
         * Then checks if isArchived was updated or not (by compare {@link PersonRequest} isArchived flag and flag received from database).
         */
        @Test
        public void update_should_update_person_is_archived_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .isArchived(true)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM persons_person WHERE person_id = " + personId, Boolean.class);
            assertEquals(rq.isArchived(), isArchived);
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request with null person isArchived.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update by id with null person isArchived.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_null_is_archived_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .isArchived(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#update(PersonRequest)} should update {@link Person} isUnregistered flag.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update isUnregistered flag by id.
         * Then checks if isUnregistered was updated or not (by compare {@link PersonRequest} isUnregistered flag and flag received from database).
         */
        @Test
        public void update_should_update_person_is_unregistered_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .isUnregistered(true)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            Boolean isUnregistered = jdbcTemplate.queryForObject("SELECT is_unregistered FROM persons_person WHERE person_id = " + personId, Boolean.class);
            assertEquals(rq.isUnregistered(), isUnregistered);
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request with null person isUnregistered.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update by id with null person isUnregistered.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_null_is_unregistered_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .isUnregistered(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#update(PersonRequest)} should update {@link Person} phone number field.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update person phone number by id.
         * Then checks if phone number was updated or not (by compare {@link PersonRequest} phone number and phone number received from database).
         */
        @Test
        public void update_should_update_person_phone_number_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .phoneNumber("777")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            String newPhoneNumber = jdbcTemplate.queryForObject("SELECT phone_number FROM persons_person WHERE person_id = " + personId + " AND is_archived IS FALSE", String.class);
            assertEquals(rq.phoneNumber(), newPhoneNumber);
        }

        /**
         * {@link PersonController#update(PersonRequest)} should update {@link Person} companyUuid field.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update person companyUuid by id.
         * Then checks if phone number was updated or not (by compare {@link PersonRequest} companyUuid and companyUuid received from database).
         */
        @Test
        public void update_should_update_person_company_uuid_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            UUID uuid = UUID.fromString("735921a7-4444-4ee4-a089-2695b68e9101");
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .companyUuid(uuid)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            UUID companyUuid = jdbcTemplate.queryForObject("SELECT company_uuid FROM persons_person WHERE person_id = " + personId + " AND is_archived IS FALSE", UUID.class);
            assertEquals(rq.companyUuid(), companyUuid);
        }

        /**
         * {@link PersonController#update(PersonRequest)} should update {@link Person} surname field.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update person surname by id.
         * Then checks if surname was updated or not (by compare {@link PersonRequest} surname and surname received from database).
         */
        @Test
        public void update_should_update_person_surname_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE surname = 'surname_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .surname("Petrov")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            String surname = jdbcTemplate.queryForObject("SELECT surname FROM persons_person WHERE person_id = " + personId + " AND is_archived IS FALSE", String.class);
            assertEquals(rq.surname(), surname);
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request with null person surname.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update by id with null person name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_null_surname_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE surname = 'surname_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .surname(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request with blank person surname.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update by id with blank person surname.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_blank_surname_test() throws Exception {
            long toolId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE surname = 'surname_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(toolId)
                    .surname("  ")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request with empty person surname.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update by id with empty person surname.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_empty_surname_test() throws Exception {
            long toolId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE surname = 'surname_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(toolId)
                    .surname("")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#update(PersonRequest)} should update {@link Person} name field.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update person name by id.
         * Then checks if name was updated or not (by compare {@link PersonRequest} name and personName received from database).
         */
        @Test
        public void update_should_update_person_name_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .name("Alex")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            String newPersonName = jdbcTemplate.queryForObject("SELECT name FROM persons_person WHERE person_id = " + personId + " AND is_archived IS FALSE", String.class);
            assertEquals(rq.name(), newPersonName);
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request with null person name.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update by id with null person name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_null_name_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .name(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request with blank person name.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update by id with blank person name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_blank_name_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .name("  ")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request with empty person name.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update by id with empty person name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_empty_name_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .name("")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#update(PersonRequest)} should update {@link Person} patronymic field.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update person phone number by id.
         * Then checks if patronymic was updated or not (by compare {@link PersonRequest} patronymic and patronymic received from database).
         */
        @Test
        public void update_should_update_person_patronymic_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .patronymic("Petrovic")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            String patronymic = jdbcTemplate.queryForObject("SELECT patronymic FROM persons_person WHERE person_id = " + personId + " AND is_archived IS FALSE", String.class);
            assertEquals(rq.patronymic(), patronymic);
        }

        /**
         * {@link PersonController#update(PersonRequest)} should update {@link Person} jobTitle field.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update person jobTitle by id.
         * Then checks if jobTitle was updated or not (by compare {@link PersonRequest} jobTitle and jobTitle received from database).
         */
        @Test
        public void update_should_update_person_job_title_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .jobTitle("Courier")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            String jobTitle = jdbcTemplate.queryForObject("SELECT job_title FROM persons_person WHERE person_id = " + personId + " AND is_archived IS FALSE", String.class);
            assertEquals(rq.jobTitle(), jobTitle);
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request with null person jobTitle.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update by id with null person jobTitle.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_null_job_title_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .jobTitle(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request with blank person jobTitle.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update by id with blank person jobTitle.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_blank_job_title_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .jobTitle("  ")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request with empty person jobTitle.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update by id with empty person jobTitle.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_empty_job_title_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .jobTitle("")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request if person with searching id not exist in database.
         * Test send request for update by not existing id.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_not_existing_id_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .id(-1L)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request with null person labels.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update by id with null person labels.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_null_labels_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .labels(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#update(PersonRequest)} should return bad request with null person roles.
         * Test finds existing person id in database with jdbcTemplate.
         * Then send request for update by id with null person roles.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_null_roles_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            PersonRequest rq = getDefaultPersonRequest()
                    .id(personId)
                    .roles(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class SaveTests {
        /**
         * {@link PersonController#save(PersonRequest)} should save {@link Person} object.
         * Test checks if person with given name not exists in database.
         * Then sends request to create new person and checks status equals created.
         * Then receive number of persons exists in database with new name.
         * Then checks if number of persons with new name equals one (new person saved to database).
         */
        @Test
        public void save_should_save_new_person_test() throws Exception {
            String personName = "Joshua";
            Long countPersons = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_person WHERE name = '" + personName + "' AND is_archived IS FALSE", Long.class);
            assertEquals(0, countPersons);
            PersonRequest rq = getDefaultPersonRequest()
                    .name(personName)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isCreated());

            Long countSavedPersons = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_person WHERE name = '" + personName + "' AND is_archived IS FALSE", Long.class);
            assertEquals(1L, countSavedPersons);
        }

        /**
         * {@link PersonController#save(PersonRequest)} should not save {@link Person} object if person name is null.
         * Test sends request to create new person with null person name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_name_null_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .name(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#save(PersonRequest)} should not save {@link Person} object if person name is empty.
         * Test sends request to create new person with empty person name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_name_empty_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .name("")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#save(PersonRequest)} should not save {@link Person} object if person name is blank.
         * Test sends request to create new person with blank person name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_name_blank_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .name("  ")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#save(PersonRequest)} should not save {@link Person} object if person surname is null.
         * Test sends request to create new person with null person surname.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_surname_null_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .surname(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#save(PersonRequest)} should not save {@link Person} object if person surname is empty.
         * Test sends request to create new person with empty person surname.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_surname_empty_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .surname("")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#save(PersonRequest)} should not save {@link Person} object if person surname is blank.
         * Test sends request to create new person with blank person surname.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_surname_blank_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .surname("  ")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#save(PersonRequest)} should not save {@link Person} object if person jobTitle is null.
         * Test sends request to create new person with null person jobTitle.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_job_title_null_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .jobTitle(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#save(PersonRequest)} should not save {@link Person} object if person jobTitle is empty.
         * Test sends request to create new person with empty person jobTitle.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_job_title_empty_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .jobTitle("")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#save(PersonRequest)} should not save {@link Person} object if person jobTitle is blank.
         * Test sends request to create new person with blank person jobTitle.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_job_title_blank_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .jobTitle("  ")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#save(PersonRequest)} should not save {@link Person} object if archived flag is null.
         * Test sends request to create new person with null archived flag.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_is_archived_null_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .isArchived(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#save(PersonRequest)} should not save {@link Person} object if unregistered flag is null.
         * Test sends request to create new person with null unregistered flag.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_is_unregistered_null_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .isArchived(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#save(PersonRequest)} should not save {@link Person} object if labels is null.
         * Test sends request to create new person with null labels.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_labels_null_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .labels(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link PersonController#save(PersonRequest)} should not save {@link Person} object if roles is null.
         * Test sends request to create new person with null roles.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_roles_null_test() throws Exception {
            PersonRequest rq = getDefaultPersonRequest()
                    .roles(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }
    }
}