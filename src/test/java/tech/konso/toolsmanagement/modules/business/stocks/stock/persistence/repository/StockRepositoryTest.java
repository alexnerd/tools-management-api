package tech.konso.toolsmanagement.modules.business.stocks.stock.persistence.repository;

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
public class StockRepositoryTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StockRepository repository;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e9801', 'name_1', 'address_1')");
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e9802', 'name_2', 'address_2')");
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e9803', 'name_3', 'address_3')");
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e9804', 'name_4', 'address_4')");
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e9805', 'name_5', 'address_5')");
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e9806', 'name_6', 'address_6')");
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address, is_archived) VALUES ('935921a7-692e-4ee4-a089-2695b68e9807', 'name_7', 'address_7', 'true')");
    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM stocks_stock");
    }

    @Test
    public void validate_entity_test() {
        repository.findAll();
    }
}