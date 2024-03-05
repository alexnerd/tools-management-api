package tech.konso.toolsmanagement.modules.business.tools.comment.service.mappers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import tech.konso.toolsmanagement.PostgreSQLContainerExtension;
import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentRequest;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.dao.Comment;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.Tool;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for StocksDtoMapper. Test for mapping fields and null values.
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
public class CommentEntityMapperTest {

    @Autowired
    private CommentEntityMapper mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('tool_1', 'OWN', '935921a7-692e-4ee4-a089-8885b68e9801')");
    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM tools_tool");
    }

    private static final String CONTENT = "Content";
    private static final UUID PERSON_UUID = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");

    /**
     * Create {@link CommentRequest} object with required non-null fields.
     */
    private CommentRequest getCommentRequest() {
        Long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1'", Long.class);
        return CommentRequest.builder()
                .toolId(toolId)
                .content(CONTENT)
                .personUuid(PERSON_UUID)
                .build();
    }

    @Nested
    class ToEntityTests {

        /**
         * {@link CommentEntityMapper#toEntity(Comment, CommentRequest)} should map {@link Tool}.
         * Test creates object {@link CommentRequest} with tool id from database and then try to map it on {@link Comment} object.
         * Then checks equality tool id from comment with tool id from request.
         */
        @Test
        public void to_entity_should_map_tool_id() {
            CommentRequest rq = getCommentRequest();

            Comment comment = mapper.toEntity(new Comment(), rq);

            assertEquals(comment.getTool().getId(), rq.toolId());
        }

        /**
         * {@link CommentEntityMapper#toEntity(Comment, CommentRequest)} should map content.
         * Test creates object {@link CommentRequest} with content and then try to map it on {@link Comment} object.
         * Then checks equality comment from comment with comment from request.
         */
        @Test
        public void to_entity_should_map_content() {
            CommentRequest rq = getCommentRequest();

            Comment comment = mapper.toEntity(new Comment(), rq);

            assertEquals(comment.getContent(), rq.content());
        }

        /**
         * {@link CommentEntityMapper#toEntity(Comment, CommentRequest)} should map person uuid.
         * Test creates object {@link CommentRequest} with person uuid and then try to map it on {@link Comment} object.
         * Then checks equality person uuid from comment with person uuid from request.
         */
        @Test
        public void to_entity_should_map_person_uuid() {
            CommentRequest rq = getCommentRequest();

            Comment comment = mapper.toEntity(new Comment(), rq);

            assertEquals(comment.getPersonUuid(), rq.personUuid());
        }
    }

    @Nested
    class UpdateContentTests {
        /**
         * {@link CommentEntityMapper#updateEntity(Comment, CommentRequest)} should update content.
         * Test creates object {@link CommentRequest} with content and then try to update it in {@link Comment} object.
         * Then checks equality content from comment with content from request.
         */
        @Test
        public void update_content_should_update_content() {
            CommentRequest rq = getCommentRequest();

            Comment comment = mapper.updateEntity(new Comment(), rq);

            assertEquals(comment.getContent(), rq.content());
        }
    }
}
