package tech.konso.toolsmanagement.modules.persons.business.role.persistence.repository;

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
public class RoleRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RoleRepository repository;

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

    @Test
    public void validate_entity_test() {
        repository.findAll();
    }

}

