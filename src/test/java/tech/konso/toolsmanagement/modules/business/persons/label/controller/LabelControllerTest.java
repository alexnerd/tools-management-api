package tech.konso.toolsmanagement.modules.business.persons.label.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import tech.konso.toolsmanagement.modules.business.persons.label.controller.dto.LabelFilterResponse;
import tech.konso.toolsmanagement.modules.business.persons.label.controller.dto.LabelRequest;
import tech.konso.toolsmanagement.modules.business.persons.label.persistence.dao.Label;
import tech.konso.toolsmanagement.modules.business.persons.label.service.LabelService;
import tech.konso.toolsmanagement.modules.business.persons.commons.AbstractControllerTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Label controller layer tests.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */
public class LabelControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('label_1')");
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('label_2')");
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('label_3')");
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('label_4')");
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('label_5')");
        jdbcTemplate.update("INSERT INTO persons_label (name, is_archived) VALUES ('label_6',  'true')");
    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM persons_label");
    }

    private String urlEndpoint() {
        return url + "/v1/persons/labels";
    }

    private LabelRequest.LabelRequestBuilder getDefaultLabelRequest() {
        return LabelRequest.builder()
                .name("Important")
                .isArchived(false);
    }

    /**
     * {@link LabelController#find(Long)} should return {@link Label} by id from database.
     * Test checks status code 200 and equality labelId (received from jdbcTemplate request)
     * with id of label object received from {@link LabelService#findById(Long)} and name.
     */
    @Test
    public void find_should_return_label_test() throws Exception {
        long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = 'label_1' AND is_archived IS FALSE", Long.class);

        mockMvc.perform(get(urlEndpoint() + "/" + labelId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        Label.class,
                        dto -> dto.getId() == labelId && dto.getName().equals("label_1")
                )));
    }

    /**
     * {@link LabelController#find(Long)} should return bad request if {@link Label} with id not exist in database.
     * Test try to find label whit id = -1 (negative number guaranties, that no such id exists in database)
     * and check if controller return bad request with detailed error message in header.
     */
    @Test
    public void find_should_return_bad_request_test() throws Exception {
        long labelId = -1L;

        mockMvc.perform(get(urlEndpoint() + "/" + labelId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().stringValues("detail", "Label not found id: " + labelId));
    }

    /**
     * {@link LabelController#findAll(int, int, String, Boolean, String)}
     * should return bad request with incorrect page number. Test try to search all labels whit page = 0
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
     * {@link LabelController#findAll(int, int, String, Boolean, String)}
     * should return bad request with incorrect size number. Test try to search all labels whit size = 99999
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
     * {@link LabelController#findAll(int, int, String, Boolean, String)}  without filters should return all not archived labels.
     * Test counts all not archived labels from the database.
     * Then test make request to find all labels and checks if it returns the same number, as plane jdbc request.
     */
    @Test
    public void findAll_should_return_labels_without_filters_test() throws Exception {
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_label WHERE is_archived IS FALSE", Long.class);
        String tail = "?page=1&size=20";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        LabelFilterResponse.class,
                        dto -> dto.totalItems() == count)
                ));
    }

    /**
     * {@link LabelController#findAll(int, int, String, Boolean, String)}  with archived filter,
     * should return all archived labels.
     * Test counts all archived labels from the database.
     * Then test make request to find all labels and checks if it returns the same number, as plane jdbc request.
     */
    @Test
    public void findAll_should_filter_archived_labels_test() throws Exception {
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_label WHERE is_archived IS TRUE", Long.class);
        String tail = "?page=1&size=20&isArchived=true";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        LabelFilterResponse.class,
                        dto -> dto.totalItems() == count)
                ));
    }

    /**
     * {@link LabelController#findAll(int, int, String, Boolean, String)}  with not archived filter,
     * should return all not archived labels.
     * Test counts all not archived labels from the database.
     * Then test make request to find all labels and checks if it returns the same number, as plane jdbc request.
     */
    @Test
    public void findAll_should_filter_not_archived_labels_test() throws Exception {
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_label WHERE is_archived IS FALSE", Long.class);
        String tail = "?page=1&size=20&isArchived=false";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        LabelFilterResponse.class,
                        dto -> dto.totalItems() == count)
                ));
    }

    /**
     * {@link LabelController#findAll(int, int, String, Boolean, String)}  with filter by full name,
     * should return label with this name.
     * Test counts all label with concrete name.
     * Then test make request to find all labels and checks if it returns the same number, as plane jdbc request,
     * and checks if returns name equals with predefined name.
     */
    @Test
    public void findAll_should_filter_full_name_labels_test() throws Exception {
        String labelName = "label_1";
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_label WHERE name = '" + labelName + "' AND is_archived IS FALSE", Long.class);
        String tail = "?page=1&size=20&name=" + labelName;

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        LabelFilterResponse.class,
                        dto -> dto.totalItems() == count && dto.labels().get(0).getName().equals(labelName))
                ));
    }

    /**
     * {@link LabelController#findAll(int, int, String, Boolean, String)}  with filter by part of name,
     * should return label with this name.
     * Test counts all label with like filter by name.
     * Then test make request to find all labels and checks if it returns the same number, as plane jdbc request.
     */
    @Test
    public void findAll_should_filter_like_name_labels_test() throws Exception {
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('Important')");
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('DDLABEDDD')");
        String labelName = "lab";
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_label WHERE LOWER (name) LIKE '%" + labelName + "%' AND is_archived IS FALSE", Long.class);
        String tail = "?page=1&size=20&name=" + labelName;

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        LabelFilterResponse.class,
                        dto -> dto.totalItems() == count)
                ));
    }

    /**
     * {@link LabelController#findAll(int, int, String, Boolean, String)}  with filter sorting by name asc,
     * should return sorted labels by names asc.
     * Test returns label names from database(using jdbcTemplate) ordered by name asc.
     * Then test make request to find all labels and checks if it returns the labels in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_name_asc_labels_test() throws Exception {
        List<String> labelNames = jdbcTemplate.queryForList("SELECT name FROM persons_label WHERE is_archived IS FALSE ORDER BY name ASC", String.class);
        String tail = "?page=1&size=20&sort=name,asc";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        LabelFilterResponse.class,
                        dto -> {
                            List<String> labelNamesResponse = dto.labels().stream().map(Label::getName).toList();
                            assertIterableEquals(labelNames, labelNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link LabelController#findAll(int, int, String, Boolean, String)}  with filter sorting by name desc,
     * should return sorted labels by name desc.
     * Test returns label names from database(using jdbcTemplate) ordered by name desc.
     * Then test make request to find all labels and checks if it returns the labels in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_name_desc_labels_test() throws Exception {
        List<String> labelNames = jdbcTemplate.queryForList("SELECT name FROM persons_label WHERE is_archived IS FALSE ORDER BY name DESC", String.class);
        String tail = "?page=1&size=20&sort=name,desc";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        LabelFilterResponse.class,
                        dto -> {
                            List<String> labelNamesResponse = dto.labels().stream().map(Label::getName).toList();
                            assertIterableEquals(labelNames, labelNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link LabelController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date asc,
     * should return sorted labels by create date asc.
     * Test returns label names from database(using jdbcTemplate) ordered by created date asc.
     * Then test make request to find all labels and checks if it returns the labels in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_create_date_asc_labels_test() throws Exception {
        List<String> labelNames = jdbcTemplate.queryForList("SELECT name FROM persons_label WHERE is_archived IS FALSE ORDER BY created_at ASC", String.class);
        String tail = "?page=1&size=20&sort=createdAt,asc";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        LabelFilterResponse.class,
                        dto -> {
                            List<String> labelNamesResponse = dto.labels().stream().map(Label::getName).toList();
                            assertIterableEquals(labelNames, labelNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link LabelController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date desc,
     * should return sorted labels by create date desc.
     * Test returns label names from database(using jdbcTemplate) ordered by created date desc.
     * Then test make request to find all labels and checks if it returns the labels in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_create_date_desc_labels_test() throws Exception {
        List<String> labelNames = jdbcTemplate.queryForList("SELECT name FROM persons_label WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);

        mockMvc.perform(get(urlEndpoint() + "?page=1&size=20&sort=createdAt,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        LabelFilterResponse.class,
                        dto -> {
                            List<String> labelNamesResponse = dto.labels().stream().map(Label::getName).toList();
                            assertIterableEquals(labelNames, labelNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link LabelController#findAll(int, int, String, Boolean, String)}  without filter by default
     * should return sorted labels by create date desc.
     * Test returns label names from database(using jdbcTemplate) ordered by created date desc.
     * Then test make request to find all labels and checks if it returns the labels in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_create_date_desc_labels_by_default_test() throws Exception {
        List<String> labelNames = jdbcTemplate.queryForList("SELECT name FROM persons_label WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);
        String tail = "?page=1&size=20";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        LabelFilterResponse.class,
                        dto -> {
                            List<String> labelNamesResponse = dto.labels().stream().map(Label::getName).toList();
                            assertIterableEquals(labelNames, labelNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link LabelController#update(LabelRequest)}  should update {@link Label} name field.
     * Test finds existing label id in database with jdbcTemplate.
     * Then send request for update label name by id.
     * Then checks if name was updated or not (by compare {@link LabelRequest} name and labelName received from database).
     */
    @Test
    public void update_should_update_label_name_test() throws Exception {
        long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = 'label_1' AND is_archived IS FALSE", Long.class);
        LabelRequest rq = getDefaultLabelRequest()
                .id(labelId)
                .build();

        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isNoContent());

        String newLabelName = jdbcTemplate.queryForObject("SELECT name FROM persons_label WHERE label_id = " + labelId + " AND is_archived IS FALSE", String.class);
        assertEquals(rq.name(), newLabelName);
    }

    /**
     * {@link LabelController#update(LabelRequest)} should update {@link Label} isArchived flag.
     * Test finds existing label id in database with jdbcTemplate.
     * Then send request for update isArchived flag by id.
     * Then checks if isArchived was updated or not (by compare {@link LabelRequest} isArchived flag and flag received from database).
     */
    @Test
    public void update_should_update_label_is_archived_test() throws Exception {
        long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = 'label_1' AND is_archived IS FALSE", Long.class);
        LabelRequest rq = getDefaultLabelRequest()
                .id(labelId)
                .isArchived(true)
                .build();

        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isNoContent());

        Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM persons_label WHERE label_id = " + labelId, Boolean.class);
        assertEquals(rq.isArchived(), isArchived);
    }

    /**
     * {@link LabelController#update(LabelRequest)} should return bad request with null label name.
     * Test finds existing label id in database with jdbcTemplate.
     * Then send request for update by id with null label name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_null_name_test() throws Exception {
        long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = 'label_1' AND is_archived IS FALSE", Long.class);
        LabelRequest rq = getDefaultLabelRequest()
                .id(labelId)
                .name(null)
                .build();

        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link LabelController#update(LabelRequest)} should return bad request with blank label name.
     * Test finds existing label id in database with jdbcTemplate.
     * Then send request for update by id with blank label name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_blank_name_test() throws Exception {
        long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = 'label_1' AND is_archived IS FALSE", Long.class);
        LabelRequest rq = getDefaultLabelRequest()
                .id(labelId)
                .name("  ")
                .build();

        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link LabelController#update(LabelRequest)} should return bad request with empty label name.
     * Test finds existing label id in database with jdbcTemplate.
     * Then send request for update by id with empty label name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_empty_name_test() throws Exception {
        long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = 'label_1' AND is_archived IS FALSE", Long.class);
        LabelRequest rq = getDefaultLabelRequest()
                .id(labelId)
                .name("")
                .build();

        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link LabelController#update(LabelRequest)} should return bad request if label name already exists in database.
     * Test finds existing label name in database with jdbcTemplate.
     * Then finds another label by id with different in database with jdbcTemplate.
     * Then send request for update by id with existing label name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_existing_name_test() throws Exception {
        String existingLabelName = jdbcTemplate.queryForObject("SELECT name FROM persons_label WHERE name = 'label_1' AND is_archived IS FALSE", String.class);
        long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = 'label_2' AND is_archived IS FALSE", Long.class);
        LabelRequest rq = getDefaultLabelRequest()
                .id(labelId)
                .name(existingLabelName)
                .build();

        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link LabelController#update(LabelRequest)} should return bad request if label with searching id not exist in database.
     * Test send request for update by not existing id.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_not_existing_id_test() throws Exception {
        LabelRequest rq = getDefaultLabelRequest()
                .id(-1L)
                .name("test")
                .build();

        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link LabelController#save(LabelRequest)} should save {@link Label} object.
     * Test checks if labels with given name not exists in database.
     * Then sends request to create new label and checks status equals created.
     * Then receive number labels exists in database with new name.
     * Then cheks if number labels with new name equals one (new label saved to database).
     */
    @Test
    public void save_should_save_new_label_test() throws Exception {
        String labelName = "Important";
        Long countLabels = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_label WHERE name = '" + labelName + "' AND is_archived IS FALSE", Long.class);
        assertEquals(0, countLabels);

        LabelRequest rq = getDefaultLabelRequest()
                .name(labelName)
                .build();

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isCreated());

        Long countSavedLabels = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_label WHERE name = '" + labelName + "' AND is_archived IS FALSE", Long.class);
        assertEquals(1L, countSavedLabels);
    }

    /**
     * {@link LabelController#save(LabelRequest)} should not save {@link Label} object if brnad already exists in database.
     * Test sends request to create new label with already existing label name in database.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_label_exists_test() throws Exception {
        LabelRequest rq = getDefaultLabelRequest()
                .name("label_1")
                .build();

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link LabelController#save(LabelRequest)} should not save {@link Label} object if label name is null.
     * Test sends request to create new label with null label name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_name_null_test() throws Exception {
        LabelRequest rq = getDefaultLabelRequest()
                .name(null)
                .build();

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link LabelController#save(LabelRequest)} should not save {@link Label} object if label name is empty.
     * Test sends request to create new label with empty label name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_name_empty_test() throws Exception {
        LabelRequest rq = getDefaultLabelRequest()
                .name("")
                .build();

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link LabelController#save(LabelRequest)} should not save {@link Label} object if label name is blank.
     * Test sends request to create new label with blank label name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_name_blank_test() throws Exception {
        LabelRequest rq = getDefaultLabelRequest()
                .name("   ")
                .build();

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link LabelController#save(LabelRequest)} should not save {@link Label} object if archived flag is null.
     * Test sends request to create new label with null archived flag.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_is_archived_null_test() throws Exception {
        LabelRequest rq = getDefaultLabelRequest()
                .isArchived(null)
                .build();

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }
}

