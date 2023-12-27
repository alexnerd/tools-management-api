package tech.konso.toolsmanagement.modules.business.tools.comment.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentFilterInfo;
import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentFilterResponse;
import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentRequest;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.dao.Comment;
import tech.konso.toolsmanagement.modules.business.tools.commons.AbstractControllerTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tool comments controller layer tests.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */
public class CommentControllerTest extends AbstractControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('tool_1', 'OWN', '935921a7-692e-4ee4-a089-8885b68e9801')");
        Long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1'", Long.class);
        jdbcTemplate.update("INSERT INTO tools_comment (tool_id, content, person_uuid) VALUES ('" + toolId + "', 'comment_1', '935921a7-692e-4ee4-a089-2695b68e9802')");
        jdbcTemplate.update("INSERT INTO tools_comment (tool_id, content, person_uuid) VALUES ('" + toolId + "', 'comment_2', '935921a7-692e-4ee4-a089-2695b68e9802')");
        jdbcTemplate.update("INSERT INTO tools_comment (tool_id, content, person_uuid) VALUES ('" + toolId + "', 'comment_3', '935921a7-692e-4ee4-a089-2695b68e9802')");
        jdbcTemplate.update("INSERT INTO tools_comment (tool_id, content, person_uuid) VALUES ('" + toolId + "', 'comment_4', '935921a7-692e-4ee4-a089-2695b68e9805')");

    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM tools_comment");
        jdbcTemplate.update("DELETE FROM tools_tool");
    }

    private String urlEndpoint() {
        return url + "/v1/tools/comments";
    }

    private static final String CONTENT = "Content";
    private static final UUID PERSON_UUID = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");

    /**
     * Create {@link CommentRequest.CommentRequestBuilder} object with required non-null fields.
     */
    private CommentRequest.CommentRequestBuilder getDefaultCommentRequest() {
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('tool_2', 'OWN', '935921a7-692e-4ee4-a089-8885b68e9807')");
        Long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_2'", Long.class);

        return CommentRequest.builder()
                .toolId(toolId)
                .content(CONTENT)
                .personUuid(PERSON_UUID);
    }

    @Nested
    class FindAllTests {
        /**
         * {@link CommentController#findAll(int, int, long, String)}
         * should return bad request with incorrect page number. Test try to search all tools whit page = 0
         * and check if controller return bad request with detailed error message in header.
         */
        @Test
        public void findAll_with_incorrect_page_should_return_bab_request_test() throws Exception {
            String tail = "?page=0&size=20&toolId=-1";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(header().stringValues("detail", "findAll.page: must be greater than or equal to 1"));
        }

        /**
         * {@link CommentController#findAll(int, int, long, String)}
         * should return bad request with incorrect size number. Test try to search all tools whit size = 99999
         * and check if controller return bad request with detailed error message in header.
         */
        @Test
        public void findAll_without_tool_id_should_return_bab_request_test() throws Exception {
            String tail = "?page=1&size=20";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isBadRequest()).
                    andExpect(header().stringValues("detail", "Required request parameter 'toolId' for method parameter type long is not present"));
        }

        /**
         * {@link CommentController#findAll(int, int, long, String)}
         * should return bad request with incorrect size number. Test try to search all tools whit size = 99999
         * and check if controller return bad request with detailed error message in header.
         */
        @Test
        public void findAll_with_incorrect_size_should_return_bab_request_test() throws Exception {
            String tail = "?page=1&size=99999&toolId=-1";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isBadRequest()).
                    andExpect(header().stringValues("detail", "findAll.size: must be less than or equal to 50"));
        }

        /**
         * {@link CommentController#findAll(int, int, long, String)} with filter should return comments for tool with
         * desc order by create date.
         * Test returns comments from database(using jdbcTemplate) ordered by created date desc.
         * Then test make request to find all tool comments and checks if it returns the tool comments in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_created_at_desc_tools_test() throws Exception {
            Long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1'", Long.class);
            List<String> comments = jdbcTemplate.queryForList("SELECT content FROM tools_comment ORDER BY created_at DESC", String.class);
            String tail = "?page=1&size=20&sort=createdAt,desc&toolId=" + toolId;

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            CommentFilterResponse.class,
                            dto -> {
                                List<String> toolNamesResponse = dto.comments().stream().map(CommentFilterInfo::content).toList();
                                assertIterableEquals(comments, toolNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link CommentController#findAll(int, int, long, String)} without filters should return comments for tool with
         * desc order by create date.
         * Test returns comments from database(using jdbcTemplate) ordered by created date desc.
         * Then test make request to find all tool comments and checks if it returns the tool comments in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_by_default_should_sort_by_created_at_desc_tools_test() throws Exception {
            Long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1'", Long.class);
            List<String> comments = jdbcTemplate.queryForList("SELECT content FROM tools_comment ORDER BY created_at DESC", String.class);
            String tail = "?page=1&size=20&toolId=" + toolId;

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            CommentFilterResponse.class,
                            dto -> {
                                List<String> toolNamesResponse = dto.comments().stream().map(CommentFilterInfo::content).toList();
                                assertIterableEquals(comments, toolNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link CommentController#findAll(int, int, long, String)} with filter should return comments for tool with
         * asc order by create date.
         * Test returns comments from database(using jdbcTemplate) ordered by created date asc.
         * Then test make request to find all tool comments and checks if it returns the tool comments in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_created_at_asc_tools_test() throws Exception {
            Long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1'", Long.class);
            List<String> comments = jdbcTemplate.queryForList("SELECT content FROM tools_comment ORDER BY created_at ASC", String.class);
            String tail = "?page=1&size=20&sort=createdAt,asc&toolId=" + toolId;

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            CommentFilterResponse.class,
                            dto -> {
                                List<String> toolNamesResponse = dto.comments().stream().map(CommentFilterInfo::content).toList();
                                assertIterableEquals(comments, toolNamesResponse);
                                return true;
                            })
                    ));
        }
    }

    @Nested
    class DeleteTests {
        /**
         * {@link CommentController#delete(Long)} should delete comment
         * Test returns one comment id from database(using jdbcTemplate).
         * Then test make request to delete comment and checks if it returns no content and
         * checks if it not exists in database.
         */
        @Test
        public void delete_should_delete_comment_test() throws Exception {
            Long commentId = jdbcTemplate.queryForObject("SELECT comment_id FROM tools_comment LIMIT 1", Long.class);

            mockMvc.perform(delete(urlEndpoint() + "/" + commentId))
                    .andDo(print())
                    .andExpect(status().isNoContent());
            List<Long> ids = jdbcTemplate.queryForList("SELECT comment_id FROM tools_comment WHERE comment_id = " + commentId, Long.class);
            assertTrue(ids.isEmpty());
        }
    }

    @Nested
    class UpdateTests {
        /**
         * {@link CommentController#update(CommentRequest)}} should update {@link Comment} content field.
         * Test finds existing comment id in database with jdbcTemplate.
         * Then send request for update comment content by id.
         * Then checks if content was updated or not (by compare {@link CommentRequest} content and comment content received from database).
         */
        @Test
        public void update_should_update_comment_content_test() throws Exception {
            Long commentId = jdbcTemplate.queryForObject("SELECT comment_id FROM tools_comment LIMIT 1", Long.class);
            CommentRequest rq = getDefaultCommentRequest()
                    .id(commentId)
                    .content("updated_content")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            String updatedContent = jdbcTemplate.queryForObject("SELECT content FROM tools_comment WHERE comment_id = " + commentId, String.class);
            assertEquals(rq.content(), updatedContent);
        }

        /**
         * {@link CommentController#update(CommentRequest)}} should update {@link Comment} content field.
         * Test finds existing comment id in database with jdbcTemplate.
         * Then send request for update comment content by id.
         * Then checks if content was updated or not (by compare {@link CommentRequest} content and comment content received from database).
         */
        @Test
        public void update_should_return_not_found_for_unknown_comment_id_test() throws Exception {
            CommentRequest rq = getDefaultCommentRequest()
                    .id(-1L)
                    .content("updated_content")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNotFound());
        }

        /**
         * {@link CommentController#update(CommentRequest)} should return bad request with null comment content.
         * Test finds existing comment id in database with jdbcTemplate.
         * Then send request for update by id with null comment content.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_null_content_test() throws Exception {
            Long commentId = jdbcTemplate.queryForObject("SELECT comment_id FROM tools_comment LIMIT 1", Long.class);
            CommentRequest rq = getDefaultCommentRequest()
                    .id(commentId)
                    .content(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().stringValues("detail", "Tool comment must not be blank or empty"));
        }

        /**
         * {@link CommentController#update(CommentRequest)} should return bad request with empty comment content.
         * Test finds existing comment id in database with jdbcTemplate.
         * Then send request for update by id with enpty comment content.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_empty_content_test() throws Exception {
            Long commentId = jdbcTemplate.queryForObject("SELECT comment_id FROM tools_comment LIMIT 1", Long.class);
            CommentRequest rq = getDefaultCommentRequest()
                    .id(commentId)
                    .content("")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().stringValues("detail", "Tool comment must not be blank or empty"));
        }

        /**
         * {@link CommentController#update(CommentRequest)} should return bad request with blank comment content.
         * Test finds existing comment id in database with jdbcTemplate.
         * Then send request for update by id with blank comment content.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_blank_content_test() throws Exception {
            Long commentId = jdbcTemplate.queryForObject("SELECT comment_id FROM tools_comment LIMIT 1", Long.class);
            CommentRequest rq = getDefaultCommentRequest()
                    .id(commentId)
                    .content(" ")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().stringValues("detail", "Tool comment must not be blank or empty"));
        }
    }

    @Nested
    class SaveTests {
        /**
         * {@link CommentController#save(CommentRequest)}} should save {@link Comment} content field.
         * Test sends request to create new comment and checks status equals created.
         * Then receive number comments exists in database with new content.
         * Then checks if number comments with new content equals one (new comment saved to database).
         */
        @Test
        public void save_should_save_comment_content_test() throws Exception {
            CommentRequest rq = getDefaultCommentRequest()
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isCreated());

            Long countSavedComments = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_comment WHERE content = '" + rq.content() + "'", Long.class);
            assertEquals(1L, countSavedComments);
        }

        /**
         * {@link CommentController#save(CommentRequest)}} should save {@link Comment} tool id field.
         * Test sends request to create new comment and checks status equals created.
         * Then receive number comments exists in database with new tool id.
         * Then checks if number comments with new tool_id equals one (new comment saved to database).
         */
        @Test
        public void save_should_save_comment_tool_id_test() throws Exception {
            CommentRequest rq = getDefaultCommentRequest()
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isCreated());

            Long countSavedComments = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_comment WHERE tool_id = " + rq.toolId(), Long.class);
            assertEquals(1L, countSavedComments);
        }

        /**
         * {@link CommentController#save(CommentRequest)}} should save {@link Comment} person uuid field.
         * Test sends request to create new comment and checks status equals created.
         * Then receive number comments exists in database with new person_uuid.
         * Then checks if number comments with new person uuid equals one (new comment saved to database).
         */
        @Test
        public void save_should_save_comment_person_uuid_test() throws Exception {
            CommentRequest rq = getDefaultCommentRequest()
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isCreated());

            Long countSavedComments = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_comment WHERE person_uuid = '" + rq.personUuid() + "'", Long.class);
            assertEquals(1L, countSavedComments);
        }

        /**
         * {@link CommentController#save(CommentRequest)} should return bad request with null comment content.
         * Test send request to save new comment with null content.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_return_bad_request_for_null_content_test() throws Exception {
            CommentRequest rq = getDefaultCommentRequest()
                    .content(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().stringValues("detail", "Tool comment must not be blank or empty"));
        }

        /**
         * {@link CommentController#save(CommentRequest)} should return bad request with empty comment content.
         * Test send request to save new comment with empty content.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_return_bad_request_for_empty_content_test() throws Exception {
            CommentRequest rq = getDefaultCommentRequest()
                    .content("")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().stringValues("detail", "Tool comment must not be blank or empty"));
        }

        /**
         * {@link CommentController#save(CommentRequest)} should return bad request with blank comment content.
         * Test send request to save new comment with blank content.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_return_bad_request_for_blank_content_test() throws Exception {
            CommentRequest rq = getDefaultCommentRequest()
                    .content(" ")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().stringValues("detail", "Tool comment must not be blank or empty"));

        }

        /**
         * {@link CommentController#save(CommentRequest)} should return bad request with null tool id.
         * Test send request to save new comment with null tool id.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_return_bad_request_for_null_tool_id_test() throws Exception {
            CommentRequest rq = getDefaultCommentRequest()
                    .toolId(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().stringValues("detail", "Tool id must not be null"));
        }

        /**
         * {@link CommentController#save(CommentRequest)} should return bad request with null person uuid.
         * Test send request to save new comment with null person uuid.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_return_bad_request_for_null_person_uuid_test() throws Exception {
            CommentRequest rq = getDefaultCommentRequest()
                    .personUuid(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().stringValues("detail", "Person UUID must not be null"));
        }
    }
}
