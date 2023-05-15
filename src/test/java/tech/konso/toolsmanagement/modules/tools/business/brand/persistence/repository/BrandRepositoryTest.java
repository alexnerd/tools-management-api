package tech.konso.toolsmanagement.modules.tools.business.brand.persistence.repository;

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
public class BrandRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BrandRepository repository;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('brand_1')");
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('brand_2')");
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('brand_3')");
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('brand_4')");
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('brand_5')");
        jdbcTemplate.update("INSERT INTO tools_brand (name, is_archived) VALUES ('brand_6',  'true')");

    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM tools_brand");
    }

    @Test
    public void validate_entity_test() {
        repository.findAll();
    }

}