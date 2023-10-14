package tech.konso.toolsmanagement.modules.persons.business.person.persistence.repository;

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
public class PersonRepositoryTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PersonRepository repository;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO persons_person (uuid, surname, name, job_title) VALUES ('935921a7-692e-4ee4-a089-2695b68e9801', 'surname_1', 'name_1', 'job_title_1')");
        jdbcTemplate.update("INSERT INTO persons_person (uuid, surname, name, job_title) VALUES ('935921a7-692e-4ee4-a089-2695b68e9802', 'surname_2', 'name_2', 'job_title_2')");
        jdbcTemplate.update("INSERT INTO persons_person (uuid, surname, name, job_title) VALUES ('935921a7-692e-4ee4-a089-2695b68e9803', 'surname_3', 'name_3', 'job_title_3')");
        jdbcTemplate.update("INSERT INTO persons_person (uuid, surname, name, job_title) VALUES ('935921a7-692e-4ee4-a089-2695b68e9804', 'surname_4', 'name_4', 'job_title_4')");
        jdbcTemplate.update("INSERT INTO persons_person (uuid, surname, name, job_title) VALUES ('935921a7-692e-4ee4-a089-2695b68e9805', 'surname_5', 'name_5', 'job_title_5')");
        jdbcTemplate.update("INSERT INTO persons_person (uuid, surname, name, job_title) VALUES ('935921a7-692e-4ee4-a089-2695b68e9806', 'surname_6', 'name_6', 'job_title_6')");
        jdbcTemplate.update("INSERT INTO persons_person (uuid, surname, name, job_title, is_archived) VALUES ('935921a7-692e-4ee4-a089-2695b68e9807', 'surname_7', 'name_7', 'job_title_7', 'true')");
    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM persons_person");
    }

    @Test
    public void validate_entity_test() {
        repository.findAll();
    }
}
