package tech.konso.toolsmanagement.modules.business.tools.tool.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import tech.konso.toolsmanagement.modules.business.tools.commons.AbstractControllerTest;
import tech.konso.toolsmanagement.modules.business.tools.tool.controller.dto.ToolFilterInfo;
import tech.konso.toolsmanagement.modules.business.tools.tool.controller.dto.ToolFilterResponse;
import tech.konso.toolsmanagement.modules.business.tools.tool.controller.dto.ToolRequest;
import tech.konso.toolsmanagement.modules.business.tools.tool.controller.dto.UploadPhotoResponse;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.Tool;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.enums.OwnershipType;
import tech.konso.toolsmanagement.modules.business.tools.tool.service.ToolService;
import tech.konso.toolsmanagement.modules.integration.facade.FileStorageFacade;
import tech.konso.toolsmanagement.modules.integration.facade.FileType;
import tech.konso.toolsmanagement.modules.integration.facade.dto.UploadResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tool controller layer tests.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */
public class ToolControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @MockBean
    private FileStorageFacade fileStorageFacade;

    private static final UUID PHOTO_UUID = UUID.fromString("3e87966b-9566-437d-8d54-2052fbb7af5f");

    private final static String PATH_TO_JPEG_FILE = "src/test/resources/photo/TEST_PHOTO.jpeg";


    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid, photo_uuid) VALUES ('tool_1', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9801', '935921a7-692e-4ee4-a089-2695b68e9801')");
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('tool_2', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9802')");
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('tool_3', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9803')");
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('tool_4', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9804')");
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('tool_5', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9805')");
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid, is_archived) VALUES ('tool_6', 'RENT', '935921a7-692e-4ee4-a089-2695b68e9806',  'true')");

    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM tools_tool_label");
        jdbcTemplate.update("DELETE FROM tools_tool");
        jdbcTemplate.update("DELETE FROM tools_label");
        jdbcTemplate.update("DELETE FROM tools_category");
        jdbcTemplate.update("DELETE FROM tools_brand");

    }

    private String urlEndpoint() {
        return url + "/v1/tools/tools";
    }

    /**
     * Create {@link ToolRequest.ToolRequestBuilder} object with required non-null fields.
     */
    private ToolRequest.ToolRequestBuilder getDefaultToolRequest() {
        return ToolRequest.builder()
                .name("new_tool")
                .ownershipType(OwnershipType.OWN.name())
                .isConsumable(false)
                .isKit(false)
                .isArchived(false)
                .labels(Collections.emptySet());
    }

    private byte[] getPhoto(String filePath) throws IOException {
        Path path = Path.of(filePath);
        return Files.readAllBytes(path);
    }

    @Nested
    class FindTests {
        /**
         * {@link ToolController#find(Long)} should return {@link Tool} by id from database.
         * Test checks status code 200 and equality toolId (received from jdbcTemplate request)
         * with id of tool object received from {@link ToolService#findById(Long)} and name.
         */
        @Test
        public void find_should_return_tool_test() throws Exception {
            long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);

            mockMvc.perform(get(urlEndpoint() + "/" + toolId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            Tool.class,
                            dto -> dto.getId() == toolId && dto.getName().equals("tool_1")
                    )));
        }

        /**
         * {@link ToolController#find(Long)} should return bad request if {@link Tool} with id not exist in database.
         * Test try to find tool whit id = -1 (negative number guaranties, that no such id exists in database)
         * and check if controller return bad request with detailed error message in header.
         */
        @Test
        public void find_should_return_bad_request_test() throws Exception {
            long toolId = -1L;

            mockMvc.perform(get(urlEndpoint() + "/" + toolId))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(header().stringValues("detail", "Tool not found id: " + toolId));
        }
    }

    @Nested
    class FindAllTests {
        /**
         * {@link ToolController#findAll(int, int, String, Boolean, String)}
         * should return bad request with incorrect page number. Test try to search all tools whit page = 0
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
         * {@link ToolController#findAll(int, int, String, Boolean, String)}
         * should return bad request with incorrect size number. Test try to search all tools whit size = 99999
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
         * {@link ToolController#findAll(int, int, String, Boolean, String)}  without filters should return all not archived tools.
         * Test counts all not archived tools from the database.
         * Then test make request to find all tools and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_return_tools_without_filters_test() throws Exception {
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            ToolFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link ToolController#findAll(int, int, String, Boolean, String)}  with archived filter,
         * should return all archived tools.
         * Test counts all archived tools from the database.
         * Then test make request to find all tools and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_filter_archived_tools_test() throws Exception {
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE is_archived IS TRUE", Long.class);
            String tail = "?page=1&size=20&isArchived=true";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            ToolFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link ToolController#findAll(int, int, String, Boolean, String)}  with not archived filter,
         * should return all not archived tools.
         * Test counts all not archived tools from the database.
         * Then test make request to find all tools and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_filter_not_archived_tools_test() throws Exception {
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20&isArchived=false";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            ToolFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link ToolController#findAll(int, int, String, Boolean, String)}  with filter by full name,
         * should return tool with this name.
         * Test counts all tool with concrete name.
         * Then test make request to find all tools and checks if it returns the same number, as plane jdbc request,
         * and checks if returns name equals with predefined name.
         */
        @Test
        public void findAll_should_filter_full_name_tools_test() throws Exception {
            String toolName = "tool_1";
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE name = '" + toolName + "' AND is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20&name=" + toolName;

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            ToolFilterResponse.class,
                            dto -> dto.totalItems() == count && dto.tools().get(0).name().equals(toolName))
                    ));
        }

        /**
         * {@link ToolController#findAll(int, int, String, Boolean, String)}  with filter by part of name,
         * should return tool with this name.
         * Test counts all tool with like filter by name.
         * Then test make request to find all tools and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_filter_like_name_tools_test() throws Exception {
            jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('Makita', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9807')");
            jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('DDBRANDDD', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9808')");
            String toolName = "rand";
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE LOWER (name) LIKE '%" + toolName + "%' AND is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20&name=" + toolName;

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            ToolFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link ToolController#findAll(int, int, String, Boolean, String)}  with filter sorting by name asc,
         * should return sorted tools by names asc.
         * Test returns tool names from database(using jdbcTemplate) ordered by name asc.
         * Then test make request to find all tools and checks if it returns the tools in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_name_asc_tools_test() throws Exception {
            List<String> toolNames = jdbcTemplate.queryForList("SELECT name FROM tools_tool WHERE is_archived IS FALSE ORDER BY name ASC", String.class);
            String tail = "?page=1&size=20&sort=name,asc";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            ToolFilterResponse.class,
                            dto -> {
                                List<String> toolNamesResponse = dto.tools().stream().map(ToolFilterInfo::name).toList();
                                assertIterableEquals(toolNames, toolNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link ToolController#findAll(int, int, String, Boolean, String)}  with filter sorting by name desc,
         * should return sorted tools by name desc.
         * Test returns tool names from database(using jdbcTemplate) ordered by name desc.
         * Then test make request to find all tools and checks if it returns the tools in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_name_desc_tools_test() throws Exception {
            List<String> toolNames = jdbcTemplate.queryForList("SELECT name FROM tools_tool WHERE is_archived IS FALSE ORDER BY name DESC", String.class);
            String tail = "?page=1&size=20&sort=name,desc";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            ToolFilterResponse.class,
                            dto -> {
                                List<String> toolNamesResponse = dto.tools().stream().map(ToolFilterInfo::name).toList();
                                assertIterableEquals(toolNames, toolNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link ToolController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date asc,
         * should return sorted tools by create date asc.
         * Test returns tool names from database(using jdbcTemplate) ordered by created date asc.
         * Then test make request to find all tools and checks if it returns the tools in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_create_date_asc_tools_test() throws Exception {
            List<String> toolNames = jdbcTemplate.queryForList("SELECT name FROM tools_tool WHERE is_archived IS FALSE ORDER BY created_at ASC", String.class);
            String tail = "?page=1&size=20&sort=createdAt,asc";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            ToolFilterResponse.class,
                            dto -> {
                                List<String> toolNamesResponse = dto.tools().stream().map(ToolFilterInfo::name).toList();
                                assertIterableEquals(toolNames, toolNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link ToolController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date desc,
         * should return sorted tools by create date desc.
         * Test returns tool names from database(using jdbcTemplate) ordered by created date desc.
         * Then test make request to find all tools and checks if it returns the tools in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_create_date_desc_tools_test() throws Exception {
            List<String> toolNames = jdbcTemplate.queryForList("SELECT name FROM tools_tool WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);

            mockMvc.perform(get(urlEndpoint() + "?page=1&size=20&sort=createdAt,desc"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            ToolFilterResponse.class,
                            dto -> {
                                List<String> toolNamesResponse = dto.tools().stream().map(ToolFilterInfo::name).toList();
                                assertIterableEquals(toolNames, toolNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link ToolController#findAll(int, int, String, Boolean, String)}  without filter by default
         * should return sorted tools by create date desc.
         * Test returns tool names from database(using jdbcTemplate) ordered by created date desc.
         * Then test make request to find all tools and checks if it returns the tools in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_create_date_desc_tools_by_default_test() throws Exception {
            List<String> toolNames = jdbcTemplate.queryForList("SELECT name FROM tools_tool WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);
            String tail = "?page=1&size=20";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            ToolFilterResponse.class,
                            dto -> {
                                List<String> toolNamesResponse = dto.tools().stream().map(ToolFilterInfo::name).toList();
                                assertIterableEquals(toolNames, toolNamesResponse);
                                return true;
                            })
                    ));
        }
    }

    @Nested
    class UpdateTests {
        /**
         * {@link ToolController#update(ToolRequest)}  should update {@link Tool} name field.
         * Test finds existing tool id in database with jdbcTemplate.
         * Then send request for update tool name by id.
         * Then checks if name was updated or not (by compare {@link ToolRequest} name and toolName received from database).
         */
        @Test
        public void update_should_update_tool_name_test() throws Exception {
            long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
            ToolRequest rq = getDefaultToolRequest()
                    .id(toolId)
                    .name("MAKITAMTK24")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            String newToolName = jdbcTemplate.queryForObject("SELECT name FROM tools_tool WHERE tool_id = " + toolId + " AND is_archived IS FALSE", String.class);
            assertEquals(rq.name(), newToolName);
        }

        /**
         * {@link ToolController#update(ToolRequest)} should update {@link Tool} isArchived flag.
         * Test finds existing tool id in database with jdbcTemplate.
         * Then send request for update isArchived flag by id.
         * Then checks if isArchived was updated or not (by compare {@link ToolRequest} isArchived flag and flag received from database).
         */
        @Test
        public void update_should_update_tool_is_archived_test() throws Exception {
            long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
            ToolRequest rq = getDefaultToolRequest()
                    .id(toolId)
                    .isArchived(true)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM tools_tool WHERE tool_id = " + toolId, Boolean.class);
            assertEquals(rq.isArchived(), isArchived);
        }

        /**
         * {@link ToolController#update(ToolRequest)} should return bad request with null tool name.
         * Test finds existing tool id in database with jdbcTemplate.
         * Then send request for update by id with null tool name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_null_name_test() throws Exception {
            long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
            ToolRequest rq = getDefaultToolRequest()
                    .id(toolId)
                    .name(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link ToolController#update(ToolRequest)} should return bad request with blank tool name.
         * Test finds existing tool id in database with jdbcTemplate.
         * Then send request for update by id with blank tool name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_blank_name_test() throws Exception {
            long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
            ToolRequest rq = getDefaultToolRequest()
                    .id(toolId)
                    .name("  ")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link ToolController#update(ToolRequest)} should return bad request with empty tool name.
         * Test finds existing tool id in database with jdbcTemplate.
         * Then send request for update by id with empty tool name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_empty_name_test() throws Exception {
            long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
            ToolRequest rq = getDefaultToolRequest()
                    .id(toolId)
                    .name("")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link ToolController#update(ToolRequest)} should return bad request if tool name already exists in database.
         * Test finds existing tool name in database with jdbcTemplate.
         * Then finds another tool by id with different in database with jdbcTemplate.
         * Then send request for update by id with existing tool name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_existing_name_test() throws Exception {
            String existingToolName = jdbcTemplate.queryForObject("SELECT name FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", String.class);
            long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_2' AND is_archived IS FALSE", Long.class);
            ToolRequest rq = getDefaultToolRequest()
                    .id(toolId)
                    .name(existingToolName)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());
        }

        /**
         * {@link ToolController#update(ToolRequest)} should return bad request if tool with searching id not exist in database.
         * Test send request for update by not existing id.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_not_existing_id_test() throws Exception {
            ToolRequest rq = getDefaultToolRequest()
                    .id(-1L)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link ToolController#update(ToolRequest)} should return unprocessable entity with null tool ownership.
         * Test finds existing tool id in database with jdbcTemplate.
         * Then send request for update by id with null tool ownership.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_unprocessable_entity_for_null_ownership_type_test() throws Exception {
            long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
            ToolRequest rq = getDefaultToolRequest()
                    .id(toolId)
                    .ownershipType(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link ToolController#update(ToolRequest)} should return unprocessable entity with unknown ownership.
         * Test finds existing tool id in database with jdbcTemplate.
         * Then send request for update by id with unknown tool ownership.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_unprocessable_entity_for_unknown_ownership_type_test() throws Exception {
            long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
            ToolRequest rq = getDefaultToolRequest()
                    .id(toolId)
                    .ownershipType("TEST")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class SaveTests {
        /**
         * {@link ToolController#save(ToolRequest)} should save {@link Tool} object.
         * Test checks if tools with given name not exists in database.
         * Then sends request to create new tool and checks status equals created.
         * Then receive number tools exists in database with new name.
         * Then cheks if number tools with new name equals one (new tool saved to database).
         */
        @Test
        public void save_should_save_new_tool_test() throws Exception {
            String toolName = "MAKITAMTK24";
            Long countTools = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE name = '" + toolName + "' AND is_archived IS FALSE", Long.class);
            assertEquals(0, countTools);
            ToolRequest rq = getDefaultToolRequest()
                    .name(toolName)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isCreated());

            Long countSavedTools = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE name = '" + toolName + "' AND is_archived IS FALSE", Long.class);
            assertEquals(1L, countSavedTools);
        }

        /**
         * {@link ToolController#save(ToolRequest)} should save new {@link Tool} object if name already exists in database.
         * Test sends request to create new tool with already existing tool name in database.
         * Then checks if controller response with created status code.
         */
        @Test
        public void save_should_save_new_tool_if_name_exists_test() throws Exception {
            String toolName = "tool_1";
            ToolRequest rq = getDefaultToolRequest()
                    .name(toolName)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isCreated());
        }

        /**
         * {@link ToolController#save(ToolRequest)} should not save {@link Tool} object if tool name is null.
         * Test sends request to create new tool with null tool name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_name_null_test() throws Exception {
            ToolRequest rq = getDefaultToolRequest()
                    .name(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link ToolController#save(ToolRequest)} should not save {@link Tool} object if tool name is empty.
         * Test sends request to create new tool with empty tool name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_name_empty_test() throws Exception {
            ToolRequest rq = getDefaultToolRequest()
                    .name("")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link ToolController#save(ToolRequest)} should not save {@link Tool} object if tool name is blank.
         * Test sends request to create new tool with blank tool name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_name_blank_test() throws Exception {
            ToolRequest rq = getDefaultToolRequest()
                    .name("  ")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link ToolController#save(ToolRequest)} should not save {@link Tool} object if archived flag is null.
         * Test sends request to create new tool with null archived flag.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_is_archived_null_test() throws Exception {
            ToolRequest rq = getDefaultToolRequest()
                    .isArchived(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link ToolController#save(ToolRequest)} should not save {@link Tool} object if ownership type is null.
         * Test sends request to create new tool with null ownership type.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_ownership_type_null_test() throws Exception {
            ToolRequest rq = getDefaultToolRequest()
                    .ownershipType(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link ToolController#save(ToolRequest)} should not save {@link Tool} object if ownership type is unsupported.
         * Test sends request to create new tool with unsupported ownership type.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_with_unsupported_ownership_type_test() throws Exception {
            ToolRequest rq = getDefaultToolRequest()
                    .ownershipType("TEST")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class FindPhoto {
        /**
         * {@link ToolController#findPhoto(Long)} should return photo from storage service.
         * Test try to get photo by tool id and then check status code 200, content type and
         * check if bytes of photo from storage service equals bytes from file system
         */
        @Test
        public void find_photo_should_return_photo_uuid_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE photo_uuid IS NOT NULL LIMIT 1", Long.class);
            InputStream is = new ByteArrayInputStream(getPhoto(PATH_TO_JPEG_FILE));
            InputStreamResource photo = new InputStreamResource(is);
            BDDMockito.given(fileStorageFacade.download(any(UUID.class), eq(FileType.PHOTO_TOOL))).willReturn(photo);

            mockMvc.perform(get(urlEndpoint() + "/" + personId + "/photo"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG_VALUE))
                    .andExpect(content().bytes(getPhoto(PATH_TO_JPEG_FILE)));
        }

        /**
         * {@link ToolController#findPhoto(Long)} should return bad request when photo not found.
         * Test try to get photo by tool id and then check status code bad request
         */
        @Test
        public void find_photo_should_return_bad_request_if_photo_not_found_test() throws Exception {
            long personId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE photo_uuid IS NULL LIMIT 1", Long.class);
            InputStream is = new ByteArrayInputStream(getPhoto(PATH_TO_JPEG_FILE));
            InputStreamResource photo = new InputStreamResource(is);
            BDDMockito.given(fileStorageFacade.download(any(UUID.class), eq(FileType.PHOTO_TOOL))).willReturn(photo);

            mockMvc.perform(get(urlEndpoint() + "/" + personId + "/photo"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class UploadPhoto {
        /**
         * {@link ToolController#uploadPhoto(MultipartFile)} should return photo uuid from storage service.
         * Test try to upload photo and then check status code 200, content type and if photo uuid equals uuid in mock object.
         */
        @Test
        public void upload_photo_should_return_photo_uuid_test() throws Exception {
            BDDMockito.given(fileStorageFacade.upload(any(), eq(FileType.PHOTO_TOOL))).willReturn(new UploadResponse(PHOTO_UUID, null));

            mockMvc.perform(MockMvcRequestBuilders.multipart(urlEndpoint() + "/photo")
                            .file("attachment", getPhoto(PATH_TO_JPEG_FILE)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            UploadPhotoResponse.class,
                            dto -> dto.uuid().equals(PHOTO_UUID)
                    )));
        }

        /**
         * {@link ToolController#uploadPhoto(MultipartFile)} should return bad request if photo not upload.
         * Test try to upload photo and then check status code 400.
         */
        @Test
        public void upload_photo_should_return_bad_request_if_photo_not_upload_test() throws Exception {
            BDDMockito.given(fileStorageFacade.upload(any(), eq(FileType.PHOTO_TOOL))).willReturn(new UploadResponse(null, "Some error"));

            mockMvc.perform(MockMvcRequestBuilders.multipart(urlEndpoint() + "/photo")
                            .file("attachment", getPhoto(PATH_TO_JPEG_FILE)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
