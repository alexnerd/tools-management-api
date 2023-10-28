package tech.konso.toolsmanagement.modules.business.tools.tool.persistence.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import tech.konso.toolsmanagement.PostgreSQLContainerExtension;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.Tool;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.enums.OwnershipType;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(PostgreSQLContainerExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ToolRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ToolRepository repository;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('tool_1', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9801')");
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('tool_2', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9802')");
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('tool_3', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9803')");
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('tool_4', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9804')");
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('tool_5', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9805')");
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid, is_archived) VALUES ('tool_6', 'RENT', '935921a7-692e-4ee4-a089-2695b68e9806',  'true')");

    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM tools_tool");
    }

    @Test
    public void validate_entity_test() {
        repository.findAll();
    }

    /**
     * Create {@link Tool} object with required non-null fields.
     */
    private Tool getDefaultTool() {
        Tool tool = new Tool();
        tool.setName("new_tool");
        tool.setUuid(UUID.fromString("391e24c3-db85-4d65-8973-9c1ecfa932c2"));
        tool.setOwnershipType(OwnershipType.OWN);
        tool.setIsConsumable(false);
        tool.setIsKit(false);
        tool.setIsArchived(false);

        return tool;
    }

    /**
     * Wraps field with single quote on both sides to use it is SQL query
     */
    private String wrap(String s) {
        return "'"+s+"'";
    }

    /**
     * {@link ToolRepository#save(Object)} should save {@link Tool} object.
     * Test try to save Tool.
     * Then test checks if savde tool id not null
     */
    @Test
    public void save_should_save_tool_test() {
        Tool tool = getDefaultTool();

        Tool savedTool = repository.save(tool);

        assertNotNull(savedTool.getId());
    }

    /**
     * {@link ToolRepository#save(Object)} should save {@link Tool} object even if tool name already exists in database.
     * Test try to save Tool.
     * Then test checks if saved tool id not null
     */
    @Test
    public void save_should_save_tool_with_existing_name_test() {
        String toolName = "tool_1";
        Tool tool = getDefaultTool();
        tool.setName(toolName);

        Tool savedTool = repository.save(tool);

        assertNotNull(savedTool.getId());
    }

    /**
     * {@link ToolRepository#save(Object)} should not save {@link Tool} object if tool uuid already exists in database.
     * Test try to save Tool with existing uuid in database and check if {@link DataIntegrityViolationException} is thrown.
     * hen test checks if there is no tools with null name exists in the database
     */
    @Test
    @Disabled
    public void save_should_not_save_tool_with_existing_uuid_test() {
        Tool tool = getDefaultTool();
        tool.setUuid(UUID.fromString("935921a7-692e-4ee4-a089-2695b68e9801"));

        assertThrows(DataIntegrityViolationException.class, () -> repository.save(tool));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE name IS NULL", Long.class);
        assertEquals(0L, count);
    }

    /**
     * {@link ToolRepository#save(Object)} should not save {@link Tool} object if field name is null.
     * Test try to save Tool with null field name and check if {@link DataIntegrityViolationException} is thrown.
     * Then test checks if there is no tools with null name exists in the database
     */
    @Test
    @Disabled
    public void save_should_not_save_if_tool_name_is_null_test() {
        Tool tool = getDefaultTool();
        tool.setName(null);

        assertThrows(DataIntegrityViolationException.class, () -> repository.save(tool));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE name IS NULL", Long.class);
        assertEquals(0L, count);
    }

    /**
     * {@link ToolRepository#save(Object)} should not save {@link Tool} object if field uuid is null.
     * Test try to save Tool with null field uuid and check if {@link DataIntegrityViolationException} is thrown.
     * Then test checks if there is no tools with this name exists in the database
     */
    @Test
    @Disabled
    public void save_should_not_save_if_tool_uuid_is_null_test() {
        Tool tool = getDefaultTool();
        tool.setUuid(null);

        assertThrows(DataIntegrityViolationException.class, () -> repository.save(tool));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE name = " + wrap(tool.getName()), Long.class);
        assertEquals(0L, count);
    }

    /**
     * {@link ToolRepository#save(Object)} should not save {@link Tool} object if field is consumable is null.
     * Test try to save Tool with null field is consumable and check if {@link DataIntegrityViolationException} is thrown.
     * Then test checks if there is no tools with this name exists in the database
     */
    @Test
    @Disabled
    public void save_should_not_save_if_is_consumable_is_null_test() {
        Tool tool = getDefaultTool();
        tool.setIsConsumable(null);

        assertThrows(DataIntegrityViolationException.class, () -> repository.save(tool));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE name = " + wrap(tool.getName()), Long.class);
        assertEquals(0L, count);
    }

    /**
     * {@link ToolRepository#save(Object)} should not save {@link Tool} object if field is kit is null.
     * Test try to save Tool with null field is kit and check if {@link DataIntegrityViolationException} is thrown.
     * Then test checks if there is no tools with this name exists in the database
     */
    @Test
    @Disabled
    public void save_should_not_save_if_is_kit_is_null_test() {
        Tool tool = getDefaultTool();
        tool.setIsKit(null);

        assertThrows(DataIntegrityViolationException.class, () -> repository.save(tool));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE name = " + wrap(tool.getName()), Long.class);
        assertEquals(0L, count);
    }

    /**
     * {@link ToolRepository#save(Object)} should not save {@link Tool} object if field is archived is null.
     * Test try to save Tool with null field is archived and check if {@link DataIntegrityViolationException} is thrown.
     * Then test checks if there is no tools with this name exists in the database
     */
    @Test
    @Disabled
    public void save_should_not_save_if_is_archived_is_null_test() {
        Tool tool = getDefaultTool();
        tool.setIsArchived(null);

        assertThrows(DataIntegrityViolationException.class, () -> repository.save(tool));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE name = " + wrap(tool.getName()), Long.class);
        assertEquals(0L, count);
    }

    /**
     * {@link ToolRepository#save(Object)} should not save {@link Tool} object if field is kit is null.
     * Test try to save Tool with null field is kit and check if {@link DataIntegrityViolationException} is thrown.
     * Then test checks if there is no tools with this name exists in the database
     */
    @Test
    @Disabled
    public void save_should_not_save_if_ownership_type_is_null_test() {
        Tool tool = getDefaultTool();
        tool.setOwnershipType(null);

        assertThrows(DataIntegrityViolationException.class, () -> repository.save(tool));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE name = " + wrap(tool.getName()), Long.class);
        assertEquals(0L, count);
    }

}