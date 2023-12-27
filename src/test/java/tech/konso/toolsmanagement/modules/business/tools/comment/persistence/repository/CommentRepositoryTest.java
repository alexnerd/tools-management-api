package tech.konso.toolsmanagement.modules.business.tools.comment.persistence.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import tech.konso.toolsmanagement.PostgreSQLContainerExtension;

@DataJpaTest
@ExtendWith(PostgreSQLContainerExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentRepository repository;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('tool_1', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9801')");
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

    @Test
    public void validate_entity_test() {
        repository.findAll();
    }
}
