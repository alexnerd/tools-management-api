package tech.konso.toolsmanagement.modules.business.tools.label.persistence.repository;

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
public class LabelRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LabelRepository repository;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_1')");
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_2')");
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_3')");
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_4')");
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_5')");
        jdbcTemplate.update("INSERT INTO tools_label (name, is_archived) VALUES ('label_6',  'true')");

    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM tools_label");
    }

    @Test
    public void validate_entity_test() {
        repository.findAll();
    }

}