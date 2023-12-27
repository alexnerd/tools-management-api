package tech.konso.toolsmanagement.modules.business.tools.comment.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import tech.konso.toolsmanagement.PostgreSQLContainerExtension;
import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentFilterInfo;
import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentRequest;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.dao.Comment;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.Tool;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static tech.konso.toolsmanagement.modules.business.tools.comment.persistence.specification.CommentSpecification.sortSpec;
import static tech.konso.toolsmanagement.modules.business.tools.comment.persistence.specification.CommentSpecification.toolSpec;
import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

/**
 * Comment service layer tests.
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
public class CommentServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentService service;

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

    private static final String CONTENT = "Content";
    private static final UUID PERSON_UUID = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");

    /**
     * Create {@link CommentRequest.CommentRequestBuilder} object with required non-null fields.
     */
    private CommentRequest.CommentRequestBuilder getDefaultCommentRequest() {
        Long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1'", Long.class);

        return CommentRequest.builder()
                .toolId(toolId)
                .content(CONTENT)
                .personUuid(PERSON_UUID);
    }

    @Nested
    class DeleteByIdTests {

        /**
         * {@link CommentService#deleteById(Long)} should delete {@link Comment} by id from database.
         * Test delete comment from datav=base by id
         * Then try to find deleted comment in database
         */
        @Test
        public void deleteById_should_delete_comment_test() {
            long commentId = jdbcTemplate.queryForObject("SELECT comment_id FROM tools_comment WHERE content = 'comment_1'", Long.class);

            service.deleteById(commentId);

            boolean isEmpty = jdbcTemplate.queryForList("SELECT 1 FROM tools_comment WHERE comment_id = " + commentId).isEmpty();
            assertTrue(isEmpty);
        }
    }

    @Nested
    class SaveTests {
        /**
         * {@link CommentService#save(CommentRequest)}} should save {@link Comment} object.
         * Test creates dto object {@link CommentRequest} and then using {@link CommentService#save(CommentRequest)}
         * try to save new {@link Comment} object to database.
         * Then checks returns {@link Comment} object if id not null, content and person uuid fields equals this fields
         * from dto object {@link CommentRequest}.
         */
        @Test
        public void save_should_save_comment_test() {
            CommentRequest rq = getDefaultCommentRequest().build();

            Comment savedComment = service.save(rq);

            assertNotNull(savedComment.getId());
            assertEquals(rq.content(), savedComment.getContent());
            assertEquals(rq.toolId(), Optional.ofNullable(savedComment.getTool()).map(Tool::getId).orElse(null));
            assertEquals(rq.personUuid(), savedComment.getPersonUuid());
        }

        /**
         * {@link CommentService#save(CommentRequest)}} should not save {@link Comment} object if field content is null.
         * Test try to save Comment with null content field and check if {@link Exception} is thrown.
         * Then test checks if there is no comments with null content exists in the database
         */
        @Test
        public void save_should_not_save_if_comment_content_is_null_test() {
            CommentRequest rq = getDefaultCommentRequest()
                    .content(null)
                    .build();

            assertThrows(Exception.class, () -> service.save(rq));

            Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_comment WHERE content IS NULL", Long.class);
            assertEquals(0L, count);
        }

        /**
         * {@link CommentService#save(CommentRequest)}} should not save {@link Comment} object if field tool id is null.
         * Test try to save Comment with null tool id field and check if {@link Exception} is thrown.
         * Then test checks if there is no comments with null tool id field exists in the database
         */
        @Test
        public void save_should_not_save_if_comment_tool_id_is_null_test() {
            CommentRequest rq = getDefaultCommentRequest()
                    .toolId(null)
                    .build();

            assertThrows(Exception.class, () -> service.save(rq));

            Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_comment WHERE tool_id IS NULL", Long.class);
            assertEquals(0L, count);
        }

        /**
         * {@link CommentService#save(CommentRequest)}} should not save {@link Comment} object if field person uuid is null.
         * Test try to save Comment with null person uuid field and check if {@link Exception} is thrown.
         * Then test checks if there is no comments with null person uuid field exists in the database
         */
        @Test
        public void save_should_not_save_if_comment_person_uuid_is_null_test() {
            CommentRequest rq = getDefaultCommentRequest()
                    .personUuid(null)
                    .build();

            assertThrows(Exception.class, () -> service.save(rq));

            Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_comment WHERE person_uuid IS NULL", Long.class);
            assertEquals(0L, count);
        }

    }

    @Nested
    class UpdateTests {

        /**
         * {@link CommentService#save(CommentRequest)} should update {@link Comment} content field.
         * Test finds existing comment id in database with jdbcTemplate and try to update it content
         * using {@link CommentService#save(CommentRequest)}.
         * Then checks if content was updated or not (by compare {@link CommentRequest} content and commentContent received from database).
         */
        @Test
        public void update_should_update_comment_content_test() {
            long commentId = jdbcTemplate.queryForObject("SELECT comment_id FROM tools_comment WHERE content = 'comment_1'", Long.class);
            CommentRequest rq = getDefaultCommentRequest()
                    .id(commentId)
                    .build();

            service.save(rq);

            String commentContent = jdbcTemplate.queryForObject("SELECT content FROM tools_comment WHERE comment_id = " + commentId, String.class);
            assertEquals(rq.content(), commentContent);
        }


        /**
         * {@link CommentService#save(CommentRequest)} should not update {@link Comment} if content field is null.
         * Test finds existing comment id in database with jdbcTemplate and try to update it content field
         * using {@link CommentService#save(CommentRequest)}.
         * Then checks if exception {@link Exception} was thrown.
         * Then checks if field content not changed during test.
         */
        @Test
        public void update_should_not_update_null_content_test() {
            String content = "comment_1";
            long commentId = jdbcTemplate.queryForObject("SELECT comment_id FROM tools_comment WHERE content = '" + content + "'", Long.class);
            CommentRequest rq = getDefaultCommentRequest()
                    .id(commentId)
                    .content(null)
                    .build();

            assertThrows(Exception.class, () -> service.save(rq));

            String commentContentFromDb = jdbcTemplate.queryForObject("SELECT content FROM tools_comment WHERE comment_id = " + commentId, String.class);
            assertEquals(content, commentContentFromDb);
        }
    }

    @Nested
    class FindAllTests {

        /**
         * {@link CommentService#findAll(int, int, Specification)} should return all {@link Comment} objects.
         * Test counts all comment objects in the database using jdbcTemplate
         * Then test build basic specification  and try to find comments with {@link CommentService#findAll(int, int, Specification)}
         * Then test checks if counts comments from jdbcTemplate equals totalItems from pageable result
         * received from {@link CommentService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_should_find_all_comments_test() {
            long countComments = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_comment", Long.class);
            Specification<Comment> spec = specBuilder(Comment.class).build();

            Page<CommentFilterInfo> foundedComments = service.findAll(0, 100, spec);

            assertEquals(countComments, foundedComments.getTotalElements());
        }

        /**
         * {@link CommentService#findAll(int, int, Specification)} should return {@link Comment} objects by tool id.
         * Test counts all comment objects in the database with tool id using jdbcTemplate
         * Then test build tool specification with tool id to find comments with {@link CommentService#findAll(int, int, Specification)}
         * Then test checks if counts comments from jdbcTemplate equals totalItems from pageable result
         * received from {@link CommentService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_should_find_by_tool_id_test() {
            Long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_comment WHERE tool_id IS NOT NULL LIMIT 1", Long.class);
            Long countComments = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_comment WHERE tool_id = " + toolId, Long.class);
            Specification<Comment> spec = specBuilder(toolSpec(toolId)).build();

            Page<CommentFilterInfo> foundedComments = service.findAll(0, 100, spec);

            assertEquals(countComments, foundedComments.getTotalElements());
        }

        /**
         * {@link CommentService#findAll(int, int, Specification)} should sort {@link Comment} objects by createdAt in asc order.
         * Test receives all comment from the database ordered by createdAt in asc order
         * Then test build sort specification for order comments by createdAt in asc order and try to find them with {@link CommentService#findAll(int, int, Specification)}
         * Then test checks if order of comments received from jdbcTemplate equals order of comments  from pageable result
         * received from {@link CommentService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_should_sort_asc_by_created_date_comments_test() {
            List<Long> comments = jdbcTemplate.queryForList("SELECT comment_id FROM tools_comment ORDER BY created_at ASC", Long.class);
            Specification<Comment> spec = specBuilder(sortSpec("createdat,asc")).build();

            Page<CommentFilterInfo> foundedComments = service.findAll(0, 100, spec);

            List<Long> resultComments = foundedComments.getContent().stream().map(CommentFilterInfo::id).toList();
            assertIterableEquals(comments, resultComments);
        }

        /**
         * {@link CommentService#findAll(int, int, Specification)} should sort {@link Comment} objects by createdAt in desc order.
         * Test receives all comments from the database ordered by createdAt in desc order
         * Then test build sort specification for order comments by createdAt in desc order and try to find them with {@link CommentService#findAll(int, int, Specification)}
         * Then test checks if order of comments received from jdbcTemplate equals order of comments from pageable result
         * received from {@link CommentService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_should_sort_desc_by_created_date_comments_test() {
            List<Long> comments = jdbcTemplate.queryForList("SELECT comment_id FROM tools_comment ORDER BY created_at DESC", Long.class);
            Specification<Comment> spec = specBuilder(sortSpec("createdat,desc")).build();

            Page<CommentFilterInfo> foundedComments = service.findAll(0, 100, spec);

            List<Long> resultComments = foundedComments.getContent().stream().map(CommentFilterInfo::id).toList();
            assertIterableEquals(comments, resultComments);
        }

        /**
         * {@link CommentService#findAll(int, int, Specification)} without filters, by default should sort {@link Comment} objects by createdAt in desc order.
         * Test receives all comments from the database ordered by createdAt in desc order
         * Then test build sort specification null parameter and try to find comments with {@link CommentService#findAll(int, int, Specification)}
         * Then test checks if order of comments received from jdbcTemplate equals order of comments from pageable result
         * received from {@link CommentService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_with_null_filter_should_sort_by_default_desc_by_created_date_comments_test() {
            List<Long> comments = jdbcTemplate.queryForList("SELECT comment_id FROM tools_comment ORDER BY created_at DESC", Long.class);
            Specification<Comment> spec = specBuilder(sortSpec(null)).build();

            Page<CommentFilterInfo> foundedComments = service.findAll(0, 100, spec);

            List<Long> resultComments = foundedComments.getContent().stream().map(CommentFilterInfo::id).toList();
            assertIterableEquals(comments, resultComments);
        }

        /**
         * {@link CommentService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Comment} objects by createdAt in desc order.
         * Test receives all comments from the database ordered by createdAt in desc order
         * Then test build sort specification with empty field "  " parameter and try to find comments with {@link CommentService#findAll(int, int, Specification)}
         * Then test checks if order of comments received from jdbcTemplate equals order of comments from pageable result
         * received from {@link CommentService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_with_blank_filter_should_sort_by_default_desc_by_created_date_comments_test() {
            List<Long> comments = jdbcTemplate.queryForList("SELECT comment_id FROM tools_comment ORDER BY created_at DESC", Long.class);
            Specification<Comment> spec = specBuilder(sortSpec(" ")).build();

            Page<CommentFilterInfo> foundedComments = service.findAll(0, 100, spec);

            List<Long> resultComments = foundedComments.getContent().stream().map(CommentFilterInfo::id).toList();
            assertIterableEquals(comments, resultComments);
        }

        /**
         * {@link CommentService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Comment} objects by createdAt in desc order.
         * Test receives all comments from the database ordered by createdAt in desc order
         * Then test build sort specification with unsupported parameter and try to find comments with {@link CommentService#findAll(int, int, Specification)}
         * Then test checks if order of comments received from jdbcTemplate equals order of comments from pageable result
         * received from {@link CommentService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_with_unsupported_filter_should_sort_by_default_desc_by_created_date_comments_test() {
            List<Long> comments = jdbcTemplate.queryForList("SELECT comment_id FROM tools_comment ORDER BY created_at DESC", Long.class);
            Specification<Comment> spec = specBuilder(sortSpec("unsupported_filter")).build();

            Page<CommentFilterInfo> foundedComments = service.findAll(0, 100, spec);

            List<Long> resultComments = foundedComments.getContent().stream().map(CommentFilterInfo::id).toList();
            assertIterableEquals(comments, resultComments);
        }

        /**
         * {@link CommentService#findAll(int, int, Specification)} should return comments with specified size.
         * Test creates null specification.
         * Then test with page size 1 returns pageable result from {@link CommentService#findAll(int, int, Specification)}
         * Then test checks total received items and concrete size of founded comments in concrete page.
         */
        @Test
        public void findAll_with_page_size_one_should_return_one_comment_test() {
            Specification<Comment> spec = specBuilder(sortSpec(null)).build();

            Page<CommentFilterInfo> foundedComments = service.findAll(0, 1, spec);

            assertEquals(4, foundedComments.getTotalElements());
            assertEquals(1, foundedComments.getContent().size());
        }
    }

}

