package tech.konso.toolsmanagement.modules.tools.business.tool.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import tech.konso.toolsmanagement.PostgreSQLContainerExtension;
import tech.konso.toolsmanagement.modules.tools.business.brand.persistence.dao.Brand;
import tech.konso.toolsmanagement.modules.tools.business.category.persistence.dao.Category;
import tech.konso.toolsmanagement.modules.tools.business.label.persistence.dao.Label;
import tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto.*;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.dao.Tool;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.dao.enums.OwnershipType;
import tech.konso.toolsmanagement.modules.tools.commons.exceptions.BPException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static tech.konso.toolsmanagement.modules.tools.business.tool.persistence.specification.ToolSpecification.*;
import static tech.konso.toolsmanagement.modules.tools.commons.AbstractSpecification.specBuilder;

/**
 * Tool service layer tests.
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
public class ToolServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ToolService service;

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
        jdbcTemplate.update("DELETE FROM tools_tool_label");
        jdbcTemplate.update("DELETE FROM tools_tool");
        jdbcTemplate.update("DELETE FROM tools_label");
        jdbcTemplate.update("DELETE FROM tools_category");
        jdbcTemplate.update("DELETE FROM tools_brand");

    }

    /**
     * Create {@link ToolRequest.ToolRequestBuilder} object with required non-null fields.
     */
    private ToolRequest.ToolRequestBuilder getDefaultToolRequest() {
        return ToolRequest.builder()
                .name("new_tool")
                .ownershipType(OwnershipType.OWN.name())
                .isConsumable(false)
                .isKit(false)
                .isArchived(false)
                .labels(Collections.emptySet());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and uuid field is not null.
     */
    @Test
    public void save_should_save_tool_test() {
        ToolRequest rq = getDefaultToolRequest()
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getUuid());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and name field equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_name_tool_test() {
        String toolName = "new_tool";
        ToolRequest rq = getDefaultToolRequest()
                .name(toolName)
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertEquals(rq.name(), savedTool.getName());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and isConsumable not null and field equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_is_consumable_tool_test() {
        boolean isConsumable = true;
        ToolRequest rq = getDefaultToolRequest()
                .isConsumable(isConsumable)
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getIsConsumable());
        assertEquals(rq.isConsumable(), savedTool.getIsConsumable());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and inventoryNumber not null and field equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_inventory_number_tool_test() {
        String inventoryNumber = "14-K-23MNPROD";
        ToolRequest rq = getDefaultToolRequest()
                .inventoryNumber(inventoryNumber)
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getInventoryNumber());
        assertEquals(rq.inventoryNumber(), savedTool.getInventoryNumber());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and responsibleUuid field not null and equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_responsible_uuid_tool_test() {
        UUID responsibleUuid = UUID.fromString("391e24c3-db85-4d65-8973-9c1ecfa932c2");
        ToolRequest rq = getDefaultToolRequest()
                .responsibleUuid(responsibleUuid)
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getResponsibleUuid());
        assertEquals(rq.responsibleUuid(), savedTool.getResponsibleUuid());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and projectUuid field not null, equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_project_uuid_tool_test() {
        UUID projectUuid = UUID.fromString("391e24c3-db85-4d65-8973-9c1ecfa932c2");
        ToolRequest rq = getDefaultToolRequest()
                .projectUuid(projectUuid)
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getProjectUuid());
        assertEquals(rq.projectUuid(), savedTool.getProjectUuid());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and price field not null, equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_price_tool_test() {
        BigDecimal price = new BigDecimal("23400.23");
        ToolRequest rq = getDefaultToolRequest()
                .price(price)
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getPrice());
        assertEquals(rq.price(), savedTool.getPrice());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object with ownershipType field.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and ownershipType field not null and equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_tool_ownership_type_test() {
        OwnershipType ownershipType = OwnershipType.RENT;
        ToolRequest rq = getDefaultToolRequest()
                .ownershipType(ownershipType.name())
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getOwnershipType());
        assertEquals(OwnershipType.valueOf(rq.ownershipType()), savedTool.getOwnershipType());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and rentTill field not null, equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_rent_till_test() {
        LocalDate rentTill = LocalDate.of(2023, Month.MAY, 9);
        ToolRequest rq = getDefaultToolRequest()
                .rentTill(rentTill)
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getRentTill());
        assertEquals(rq.rentTill(), savedTool.getRentTill());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and isKit field not null, equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_is_kit_test() {
        Boolean isKit = true;
        ToolRequest rq = getDefaultToolRequest()
                .isKit(isKit)
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getIsKit());
        assertEquals(rq.isKit(), savedTool.getIsKit());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and kitUuid field not null, equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_kit_uuid_test() {
        UUID kitUuid = UUID.fromString("391e24c3-db85-4d65-8973-9c1ecfa932c2");
        ToolRequest rq = getDefaultToolRequest()
                .kitUuid(kitUuid)
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getKitUuid());
        assertEquals(rq.kitUuid(), savedTool.getKitUuid());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object with brand.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object with {@link Brand} to database.
     * Then checks returns {@link Tool} object if id not null, if brand not null, name field and isArchived flag equals this fields
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_tool_with_brand_test() {
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('brand_1')");
        Long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1'", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .brandId(brandId)
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getBrand());
        assertEquals(rq.name(), savedTool.getName());
        assertEquals(rq.isArchived(), savedTool.getIsArchived());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object with category.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object with {@link Category} to database.
     * Then checks returns {@link Tool} object if id not null, if category not null, name field and isArchived flag equals this fields
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_tool_with_category_test() {
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('category_1')");
        Long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1'", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .categoryId(categoryId)
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getCategory());
        assertEquals(rq.name(), savedTool.getName());
        assertEquals(rq.isArchived(), savedTool.getIsArchived());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object with labels.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object with {@link Label} to database.
     * Then checks returns {@link Tool} object if id not null, if labels not null, is labels size is two and
     * if labels ids equals.
     */
    @Test
    public void save_should_save_tool_with_labels_test() {
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_1')");
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_2')");
        Long labelId1 = jdbcTemplate.queryForObject("SELECT label_id FROM tools_label WHERE name = 'label_1'", Long.class);
        Long labelId2 = jdbcTemplate.queryForObject("SELECT label_id FROM tools_label WHERE name = 'label_2'", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .labels(Set.of(labelId1, labelId2))
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getLabels());
        assertEquals(2, savedTool.getLabels().size());
        Set<Long> labelsIdFromDB = savedTool.getLabels().stream().map(Label::getId).sorted().collect(Collectors.toSet());
        Set<Long> labelsIdFromRq = Stream.of(labelId1, labelId2).sorted().collect(Collectors.toSet());
        assertIterableEquals(labelsIdFromRq, labelsIdFromDB);
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save {@link Tool} object.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and isArchived field not null, equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_is_archived_test() {
        boolean isArchived = true;
        ToolRequest rq = getDefaultToolRequest()
                .isArchived(isArchived)
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getId());
        assertNotNull(savedTool.getIsArchived());
        assertEquals(rq.isArchived(), savedTool.getIsArchived());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should save new {@link Tool} object
     * if field name already exists in database.
     * Test try to save Tool with existing field name.
     * Then check if two tools with the same name exists in database
     */
    @Test
    public void save_should_save_new_tool_if_name_already_exists_test() {
        String toolName = "tool_1";
        ToolRequest rq = getDefaultToolRequest()
                .name(toolName)
                .build();

        service.save(rq);

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE name = '" + toolName + "'", Long.class);
        assertEquals(2L, count);
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should not save {@link Tool} object if field name is null.
     * Test try to save Tool with null field name and check if {@link DataIntegrityViolationException} is thrown.
     * Then test checks if there is no tools with null name exists in the database
     */
    @Test
    public void save_should_not_save_if_tool_name_is_null_test() {
        ToolRequest rq = getDefaultToolRequest()
                .name(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE name IS NULL", Long.class);
        assertEquals(0L, count);
    }

    /**
     * {@link ToolService#findById(Long)} should return {@link ToolInfo} by id from database.
     * Test checks equality toolId (received from jdbcTemplate request)
     * with id of tool object received from {@link ToolService#findById(Long)}
     */
    @Test
    public void findById_should_return_tool_test() {
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);

        ToolInfo toolInfo = service.findById(toolId);

        assertEquals(toolId, toolInfo.id());
    }

    /**
     * {@link ToolService#findById(Long)} should return {@link ToolInfo} by id from database
     * with {@link Brand} object.
     * Test prepare data. Insert tool and brand objects into database.
     * Then associate tool and brand objects by brandId.
     * Test checks equality toolId (received from jdbcTemplate request).
     * Then checks if brand object not null.
     * Then checks equality brandId (received from jdbcTemplate request)
     * with id of tool object received from {@link ToolService#findById(Long)}
     */
    @Test
    public void findById_should_return_tool_with_brand_test() {
        String brandName = "brand_1";
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('brand_1')");
        Long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = '" + brandName + "'", Long.class);
        jdbcTemplate.update("UPDATE tools_tool SET brand_id = " + brandId + " WHERE tool_id = " + toolId);

        ToolInfo toolInfo = service.findById(toolId);

        assertEquals(toolId, toolInfo.id());
        assertNotNull(toolInfo.brand());
        assertEquals(brandId, toolInfo.brand().id());
    }

    /**
     * {@link ToolService#findById(Long)} should return {@link ToolInfo} by id from database
     * with {@link Brand} object.
     * Test prepare data. Insert tool and brand objects into database.
     * Then associate tool and brand objects by brandId.
     * Test checks equality toolId (received from jdbcTemplate request).
     * Then checks if brand object not null.
     * Then checks equality brandId (received from jdbcTemplate request)
     * with id of tool object received from {@link ToolService#findById(Long)}
     */
    @Test
    public void findById_should_return_tool_with_category_test() {
        String categoryName = "category_1";
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('category_1')");
        Long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = '" + categoryName + "'", Long.class);
        jdbcTemplate.update("UPDATE tools_tool SET category_id = " + categoryId + " WHERE tool_id = " + toolId);

        ToolInfo toolInfo = service.findById(toolId);

        assertEquals(toolId, toolInfo.id());
        assertNotNull(toolInfo.category());
        assertEquals(categoryId, toolInfo.category().id());
    }

    /**
     * {@link ToolService#findById(Long)} should return {@link ToolInfo} by id from database
     * with {@link Label} objects.
     * Test prepare data. Insert tool and labels objects into database.
     * Then associate tool and label objects by join table tools_tool_label.
     * Test checks equality toolId (received from jdbcTemplate request).
     * Then checks if labels object not null.
     * Then checks size of labels from tool object.
     */
    @Test
    public void findById_should_return_tool_with_labels_test() {
        String labelName = "label_1";
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_1')");
        Long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM tools_label WHERE name = '" + labelName + "'", Long.class);
        jdbcTemplate.update("INSERT INTO tools_tool_label (tool_id, label_id) VALUES (" + toolId + ", " + labelId + ")");

        ToolInfo toolInfo = service.findById(toolId);

        assertEquals(toolId, toolInfo.id());
        assertNotNull(toolInfo.labels());
        assertEquals(1, toolInfo.labels().size());
        boolean isExists = toolInfo.labels().stream()
                .map(LabelShort::name)
                .anyMatch(name -> name.equals(labelName));
        assertTrue(isExists);
    }

    /**
     * {@link ToolService#findById(Long)} should throw {@link BPException} exception
     * if {@link Tool} with id not exist in database.
     * Test try to find tool whit id = -1 (negative num,ber guaranties, that no such id exists in database)
     * and check if {@link BPException} is thrown.
     */
    @Test
    public void findById_should_throw_exception_on_not_found_tool_test() {
        long toolId = -1;

        assertThrows(BPException.class, () -> service.findById(toolId));
    }

    /**
     * {@link ToolService#save(ToolRequest)} should update {@link Tool} name field.
     * Test finds existing tool id in database with jdbcTemplate and try to update it name
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if name was updated or not (by compare {@link ToolRequest} name and toolName received from database).
     */
    @Test
    public void update_should_update_tool_name_test() {
        String toolName = "new_tool";
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .name(toolName)
                .build();

        service.save(rq);

        String updatedValue = jdbcTemplate.queryForObject("SELECT name FROM tools_tool WHERE tool_id = " + toolId, String.class);
        assertEquals(rq.name(), updatedValue);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should update {@link Tool} isConsumable field.
     * Test finds existing tool id in database with jdbcTemplate and try to update it isConsumable field
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if field was updated or not by compare value from {@link ToolRequest} and value received from database.
     */
    @Test
    public void update_should_update_is_consumable_test() {
        boolean isConsumable = true;
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .isConsumable(isConsumable)
                .build();

        service.save(rq);

        Boolean updatedValue = jdbcTemplate.queryForObject("SELECT is_consumable FROM tools_tool WHERE tool_id = " + toolId, Boolean.class);
        assertEquals(rq.isConsumable(), updatedValue);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should update {@link Tool} inventoryNumber field.
     * Test finds existing tool id in database with jdbcTemplate and try to update it inventoryNumber field
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if field was updated or not by compare value from {@link ToolRequest} and value received from database.
     */
    @Test
    public void update_should_update_inventory_number_test() {
        String inventoryNumber = "14-K20NMBB";
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .inventoryNumber(inventoryNumber)
                .build();

        service.save(rq);

        String updatedValue = jdbcTemplate.queryForObject("SELECT inventory_number FROM tools_tool WHERE tool_id = " + toolId, String.class);
        assertEquals(rq.inventoryNumber(), updatedValue);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should update {@link Tool} responsibleUuid field.
     * Test finds existing tool id in database with jdbcTemplate and try to update it responsibleUuid field
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if field was updated or not by compare value from {@link ToolRequest} and value received from database.
     */
    @Test
    public void update_should_update_responsible_uuid_test() {
        UUID responsibleUuid = UUID.fromString("391e24c3-db85-4d65-8973-9c1ecfa932c2");
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .responsibleUuid(responsibleUuid)
                .build();

        service.save(rq);

        UUID updatedValue = jdbcTemplate.queryForObject("SELECT responsible_uuid FROM tools_tool WHERE tool_id = " + toolId, UUID.class);
        assertEquals(rq.responsibleUuid(), updatedValue);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should update {@link Tool} projectUuid field.
     * Test finds existing tool id in database with jdbcTemplate and try to update it projectUuid field
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if field was updated or not by compare value from {@link ToolRequest} and value received from database.
     */
    @Test
    public void update_should_update_project_uuid_test() {
        UUID projectUuid = UUID.fromString("391e24c3-db85-4d65-8973-9c1ecfa932c2");
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .projectUuid(projectUuid)
                .build();

        service.save(rq);

        UUID updatedValue = jdbcTemplate.queryForObject("SELECT project_uuid FROM tools_tool WHERE tool_id = " + toolId, UUID.class);
        assertEquals(rq.projectUuid(), updatedValue);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should update {@link Tool} price field.
     * Test finds existing tool id in database with jdbcTemplate and try to update it price field
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if field was updated or not by compare value from {@link ToolRequest} and value received from database.
     */
    @Test
    public void update_should_update_price_test() {
        BigDecimal price = new BigDecimal("23200.45");
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .price(price)
                .build();

        service.save(rq);

        BigDecimal updatedValue = jdbcTemplate.queryForObject("SELECT price FROM tools_tool WHERE tool_id = " + toolId, BigDecimal.class);
        assertEquals(rq.price(), updatedValue);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should update {@link Tool} ownershipType field.
     * Test finds existing tool id in database with jdbcTemplate and try to update it ownershipType field
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if field was updated or not by compare value from {@link ToolRequest} and value received from database.
     */
    @Test
    public void update_should_update_ownership_type_test() {
        OwnershipType ownershipType = OwnershipType.RENT;
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND ownership_type = 'OWN'", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .ownershipType(ownershipType.name())
                .build();

        service.save(rq);

        OwnershipType updatedValue = jdbcTemplate.queryForObject("SELECT ownership_type FROM tools_tool WHERE tool_id = " + toolId, OwnershipType.class);
        assertEquals(OwnershipType.valueOf(rq.ownershipType()), updatedValue);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should update {@link Tool} rentTill field.
     * Test finds existing tool id in database with jdbcTemplate and try to update it rentTill field
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if field was updated or not by compare value from {@link ToolRequest} and value received from database.
     */
    @Test
    public void update_should_update_rent_till_test() {
        LocalDate rentTill = LocalDate.of(2023, Month.MAY, 10);
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .rentTill(rentTill)
                .build();

        service.save(rq);

        LocalDate updatedValue = jdbcTemplate.queryForObject("SELECT rent_till FROM tools_tool WHERE tool_id = " + toolId, LocalDate.class);
        assertEquals(rq.rentTill(), updatedValue);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should update {@link Tool} isKit field.
     * Test finds existing tool id in database with jdbcTemplate and try to update it isKit field
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if field was updated or not by compare value from {@link ToolRequest} and value received from database.
     */
    @Test
    public void update_should_update_is_kit_test() {
        boolean isKit = true;
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .isKit(isKit)
                .build();

        service.save(rq);

        Boolean updatedValue = jdbcTemplate.queryForObject("SELECT is_kit FROM tools_tool WHERE tool_id = " + toolId, Boolean.class);
        assertEquals(rq.isKit(), updatedValue);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should update {@link Tool} kitUuid field.
     * Test finds existing tool id in database with jdbcTemplate and try to update it kitUuid field
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if field was updated or not by compare value from {@link ToolRequest} and value received from database.
     */
    @Test
    public void update_should_update_kit_uuid_test() {
        UUID kitUuid = UUID.fromString("391e24c3-db85-4d65-8973-9c1ecfa932c2");
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .kitUuid(kitUuid)
                .build();

        service.save(rq);

        UUID updatedValue = jdbcTemplate.queryForObject("SELECT kit_uuid FROM tools_tool WHERE tool_id = " + toolId, UUID.class);
        assertEquals(rq.kitUuid(), updatedValue);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should update {@link Tool} category.
     * Test finds existing tool id in database with jdbcTemplate.
     * Then inserts two categories and receives its ids.
     * Then associate first category with tool.
     * Test using {@link ToolService#save(ToolRequest)} try to change category on tool.
     * Then checks if category was updated or not.
     */
    @Test
    public void update_should_update_category_test() {
        String categoryName = "category_2";
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('category_1')");
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('" + categoryName + "')");
        Long categoryId_1 = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1'", Long.class);
        Long categoryId_2 = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = '" + categoryName + "'", Long.class);
        jdbcTemplate.update("UPDATE tools_tool SET category_id = " + categoryId_1 + " WHERE tool_id = " + toolId);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .categoryId(categoryId_2)
                .build();

        service.save(rq);

        ToolInfo toolInfo = service.findById(toolId);
        assertEquals(categoryName, toolInfo.category().name());
        assertEquals(categoryId_2, toolInfo.category().id());
    }

    /**
     * {@link ToolService#save(ToolRequest)} should drop {@link Tool} category.
     * Test finds existing tool id in database with jdbcTemplate.
     * Then associate category with tool.
     * Test using {@link ToolService#save(ToolRequest)} try to drop category on tool.
     * Then checks if category was dropped.
     * Then checks if category still exists in database by checking category id before and after drop.
     */
    @Test
    public void update_should_drop_category_test() {
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('category_1')");
        Long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1'", Long.class);
        jdbcTemplate.update("UPDATE tools_tool SET category_id = " + categoryId + " WHERE tool_id = " + toolId);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .categoryId(null)
                .build();

        service.save(rq);

        ToolInfo toolInfo = service.findById(toolId);
        Long categoryIdAfterDrop = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1'", Long.class);
        assertNull(toolInfo.category());
        assertEquals(categoryId, categoryIdAfterDrop);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should update {@link Tool} brand.
     * Test finds existing tool id in database with jdbcTemplate.
     * Then inserts two brands and receives its ids.
     * Then associate first brand with tool.
     * Test using {@link ToolService#save(ToolRequest)} try to change brand on tool.
     * Then checks if brand was updated or not.
     */
    @Test
    public void update_should_update_brand_test() {
        String brandName = "brand_2";
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('brand_1')");
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('" + brandName + "')");
        Long brandId_1 = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1'", Long.class);
        Long brandId_2 = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = '" + brandName + "'", Long.class);
        jdbcTemplate.update("UPDATE tools_tool SET brand_id = " + brandId_1 + " WHERE tool_id = " + toolId);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .brandId(brandId_2)
                .build();

        service.save(rq);

        ToolInfo toolInfo = service.findById(toolId);
        assertEquals(brandName, toolInfo.brand().name());
        assertEquals(brandId_2, toolInfo.brand().id());
    }

    /**
     * {@link ToolService#save(ToolRequest)} should drop {@link Tool} brand.
     * Test finds existing tool id in database with jdbcTemplate.
     * Then associate brand with tool.
     * Test using {@link ToolService#save(ToolRequest)} try to drop brand on tool.
     * Then checks if brand was dropped.
     * Then checks if brand still exists in database by checking brand id before and after drop.
     */
    @Test
    public void update_should_drop_brand_test() {
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('brand_1')");
        Long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1'", Long.class);
        jdbcTemplate.update("UPDATE tools_tool SET brand_id = " + brandId + " WHERE tool_id = " + toolId);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .brandId(null)
                .build();

        service.save(rq);

        ToolInfo toolInfo = service.findById(toolId);
        Long brandIdAfterDrop = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1'", Long.class);
        assertNull(toolInfo.brand());
        assertEquals(brandId, brandIdAfterDrop);
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should update {@link Tool} object with labels.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to update {@link Tool} object with {@link Label}.
     * Then checks returns {@link Tool} object if labels not null, is labels size is two and if labels ids equals.
     */
    @Test
    public void update_should_update_labels_test() {
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_1')");
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_2')");
        Long labelId1 = jdbcTemplate.queryForObject("SELECT label_id FROM tools_label WHERE name = 'label_1'", Long.class);
        Long labelId2 = jdbcTemplate.queryForObject("SELECT label_id FROM tools_label WHERE name = 'label_2'", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .labels(Set.of(labelId1, labelId2))
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getLabels());
        assertEquals(2, savedTool.getLabels().size());
        Set<Long> labelsIdFromDB = savedTool.getLabels().stream().map(Label::getId).sorted().collect(Collectors.toSet());
        Set<Long> labelsIdFromRq = Stream.of(labelId1, labelId2).sorted().collect(Collectors.toSet());
        assertIterableEquals(labelsIdFromRq, labelsIdFromDB);
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should update {@link Tool} object and remove one label.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to update {@link Tool} object with one {@link Label}.
     * Then checks returns {@link Tool} object if labels not null, is labels size is one and if labels ids equals.
     */
    @Test
    public void update_should_remove_one_label_test() {
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_1')");
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_2')");
        Long labelId1 = jdbcTemplate.queryForObject("SELECT label_id FROM tools_label WHERE name = 'label_1'", Long.class);
        Long labelId2 = jdbcTemplate.queryForObject("SELECT label_id FROM tools_label WHERE name = 'label_2'", Long.class);
        jdbcTemplate.update("INSERT INTO tools_tool_label (tool_id, label_id) VALUES (" + toolId + ", " + labelId1 + ")");
        jdbcTemplate.update("INSERT INTO tools_tool_label (tool_id, label_id) VALUES (" + toolId + ", " + labelId2 + ")");
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .labels(Set.of(labelId1))
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getLabels());
        assertEquals(1, savedTool.getLabels().size());
        Set<Long> labelsIdFromDB = savedTool.getLabels().stream().map(Label::getId).sorted().collect(Collectors.toSet());
        Set<Long> labelsIdFromRq = Set.of(labelId1);
        assertIterableEquals(labelsIdFromRq, labelsIdFromDB);
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should update {@link Tool} object and remove all labels.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to update {@link Tool} object with empty {@link Label}.
     * Then checks returns {@link Tool} object if labels not null, is labels size is zero.
     */
    @Test
    public void update_should_remove_all_labels_test() {
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_1')");
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_2')");
        Long labelId1 = jdbcTemplate.queryForObject("SELECT label_id FROM tools_label WHERE name = 'label_1'", Long.class);
        Long labelId2 = jdbcTemplate.queryForObject("SELECT label_id FROM tools_label WHERE name = 'label_2'", Long.class);
        jdbcTemplate.update("INSERT INTO tools_tool_label (tool_id, label_id) VALUES (" + toolId + ", " + labelId1 + ")");
        jdbcTemplate.update("INSERT INTO tools_tool_label (tool_id, label_id) VALUES (" + toolId + ", " + labelId2 + ")");
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getLabels());
        assertEquals(0, savedTool.getLabels().size());
    }

    /**
     * {@link ToolService#save(ToolRequest)}} should update {@link Tool} object and add one label.
     * Test creates dto object {@link ToolRequest} and then using {@link ToolService#save(ToolRequest)}
     * try to update {@link Tool} object with two {@link Label}.
     * Then checks returns {@link Tool} object if labels not null, is labels size is two and if labels ids equals.
     */
    @Test
    public void update_should_add_one_label_test() {
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_1')");
        jdbcTemplate.update("INSERT INTO tools_label (name) VALUES ('label_2')");
        Long labelId1 = jdbcTemplate.queryForObject("SELECT label_id FROM tools_label WHERE name = 'label_1'", Long.class);
        Long labelId2 = jdbcTemplate.queryForObject("SELECT label_id FROM tools_label WHERE name = 'label_2'", Long.class);
        jdbcTemplate.update("INSERT INTO tools_tool_label (tool_id, label_id) VALUES (" + toolId + ", " + labelId1 + ")");
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .labels(Set.of(labelId1, labelId2))
                .build();

        Tool savedTool = service.save(rq);

        assertNotNull(savedTool.getLabels());
        assertEquals(2, savedTool.getLabels().size());
        Set<Long> labelsIdFromDB = savedTool.getLabels().stream().map(Label::getId).sorted().collect(Collectors.toSet());
        Set<Long> labelsIdFromRq = Stream.of(labelId1, labelId2).sorted().collect(Collectors.toSet());
        assertIterableEquals(labelsIdFromRq, labelsIdFromDB);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should update {@link Tool} isArchived field.
     * Test finds existing tool id in database with jdbcTemplate and try to update it isArchived flag
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if isArchived flag was updated or not (using assertTrue on field).
     */
    @Test
    public void update_should_archive_tool_test() {
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .isArchived(true)
                .build();

        service.save(rq);

        Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM tools_tool WHERE tool_id = " + toolId, Boolean.class);
        assertTrue(isArchived);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should not update {@link Tool} if name field is null.
     * Test finds existing tool id in database with jdbcTemplate and try to update it name field
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if exception {@link DataIntegrityViolationException} was thrown.
     * Then checks if field value not changed during test.
     */
    @Test
    public void update_should_not_update_null_name_test() {
        String toolName = "tool_1";
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = '" + toolName + "' AND is_archived IS FALSE", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .name(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        String toolNameFromDb = jdbcTemplate.queryForObject("SELECT name FROM tools_tool WHERE tool_id = " + toolId + " AND is_archived IS FALSE", String.class);
        assertEquals(toolName, toolNameFromDb);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should not update {@link Tool} if ownershipType field is null.
     * Test finds existing tool id in database with jdbcTemplate and try to update it ownershipType field
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if exception {@link DataIntegrityViolationException} was thrown.
     * Then checks if field value not changed during test.
     */
    @Test
    @Disabled
    public void update_should_not_update_null_ownership_type_test() {
        String toolName = "tool_1";
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = '" + toolName + "' AND is_archived IS FALSE", Long.class);
        OwnershipType valueBeforeUpdate = jdbcTemplate.queryForObject("SELECT ownership_type FROM tools_tool WHERE name = '" + toolName + "' AND is_archived IS FALSE", OwnershipType.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .ownershipType(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        OwnershipType valueAfterUpdate = jdbcTemplate.queryForObject("SELECT ownership_type FROM tools_tool WHERE tool_id = " + toolId, OwnershipType.class);
        assertNotNull(valueAfterUpdate);
        assertEquals(valueBeforeUpdate, valueAfterUpdate);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should not update {@link Tool} if isConsumable field is null.
     * Test finds existing tool id in database with jdbcTemplate and try to update it isConsumable field
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if exception {@link DataIntegrityViolationException} was thrown.
     * Then checks if field value not changed during test.
     */
    @Test
    public void update_should_not_save_if_is_consumable_is_null_test() {
        String toolName = "tool_1";
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = '" + toolName + "'", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .isConsumable(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Boolean isConsumable = jdbcTemplate.queryForObject("SELECT is_consumable FROM tools_tool WHERE tool_id = " + toolId, Boolean.class);
        assertFalse(isConsumable);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should not update {@link Tool} if isKit field is null.
     * Test finds existing tool id in database with jdbcTemplate and try to update it isKit field
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if exception {@link DataIntegrityViolationException} was thrown.
     * Then checks if field value not changed during test.
     */
    @Test
    public void update_should_not_save_if_is_kit_is_null_test() {
        String toolName = "tool_1";
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = '" + toolName + "'", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .isKit(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Boolean isKit = jdbcTemplate.queryForObject("SELECT is_kit FROM tools_tool WHERE tool_id = " + toolId, Boolean.class);
        assertFalse(isKit);
    }

    /**
     * {@link ToolService#save(ToolRequest)} should not update {@link Tool} if isArchived flag is null.
     * Test finds existing tool id in database with jdbcTemplate and try to update it isArchived flag
     * using {@link ToolService#save(ToolRequest)}.
     * Then checks if exception {@link DataIntegrityViolationException} was thrown.
     * Then checks if isArchived flag not changed during test.
     */
    @Test
    public void update_should_not_update_null_isArchived_test() {
        long toolId = jdbcTemplate.queryForObject("SELECT tool_id FROM tools_tool WHERE name = 'tool_1' AND is_archived IS FALSE", Long.class);
        ToolRequest rq = getDefaultToolRequest()
                .id(toolId)
                .isArchived(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM tools_tool WHERE tool_id = " + toolId, Boolean.class);
        assertFalse(isArchived);
    }

    /**
     * {@link ToolService#findAll(int, int, Specification)} should return all not archived {@link Tool} objects.
     * Test counts all not archived tool objects in the database using jdbcTemplate
     * Then test build isArchived specification for not archived tools and try to find them with {@link ToolService#findAll(int, int, Specification)}
     * Then test checks if counts not archived tools from jdbcTemplate equals totalItems from {@link ToolFilterResponse}
     * received from {@link ToolService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_all_not_archived_tools_test() {
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE is_archived IS FALSE", Long.class);
        Specification<Tool> spec = specBuilder(isArchivedSpec(false)).build();

        Page<ToolFilterInfo> foundedTools = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedTools.getTotalElements());
    }

    /**
     * {@link ToolService#findAll(int, int, Specification)} should return all archived {@link Tool} objects.
     * Test counts all archived tool objects in the database using jdbcTemplate
     * Then test build isArchived specification for archived tools and try to find them with {@link ToolService#findAll(int, int, Specification)}
     * Then test checks if counts archived tools from jdbcTemplate equals totalItems from {@link ToolFilterResponse}
     * received from {@link ToolService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_all_archived_tools_test() {
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE is_archived IS TRUE", Long.class);
        Specification<Tool> spec = specBuilder(isArchivedSpec(true)).build();

        Page<ToolFilterInfo> foundedTools = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedTools.getTotalElements());
    }

    /**
     * {@link ToolService#findAll(int, int, Specification)} should return {@link Tool} objects by like name pattern.
     * Test counts all tool objects in the database matches %ran% pattern using jdbcTemplate
     * Then test build like specification for name with %ran% pattern and try to find them with {@link ToolService#findAll(int, int, Specification)}
     * Then test checks if counts tools from jdbcTemplate equals totalItems from {@link ToolFilterResponse}
     * received from {@link ToolService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_by_name_tools_test() {
        String likeName = "too";
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('Makita MTK24', 'OWN', '935921a7-692e-4ee4-a089-2695b68e9807')");
        jdbcTemplate.update("INSERT INTO tools_tool (name, ownership_type, uuid) VALUES ('DDTOOLDDD', 'OWN','935921a7-692e-4ee4-a089-2695b68e9808')");
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_tool WHERE LOWER (name) LIKE '%" + likeName + "%'", Long.class);
        Specification<Tool> spec = specBuilder(likeSpec(likeName)).build();

        Page<ToolFilterInfo> foundedTools = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedTools.getTotalElements());
    }

    /**
     * {@link ToolService#findAll(int, int, Specification)} should sort {@link Tool} objects by name in asc order.
     * Test receives all tool names from the database in asc order
     * Then test build sort specification for order tools by name in asc order and try to find them with {@link ToolService#findAll(int, int, Specification)}
     * Then test checks if order of tool names received from jdbcTemplate equals order of tool names from {@link ToolFilterResponse}
     * received from {@link ToolService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_asc_by_name_tools_test() {
        List<String> toolNames = jdbcTemplate.queryForList("SELECT name FROM tools_tool ORDER BY name ASC", String.class);
        Specification<Tool> spec = specBuilder(sortSpec("name,asc")).build();

        Page<ToolFilterInfo> foundedTools = service.findAll(0, 100, spec);

        List<String> resultToolNames = foundedTools.getContent().stream().map(ToolFilterInfo::name).toList();
        assertIterableEquals(toolNames, resultToolNames);
    }

    /**
     * {@link ToolService#findAll(int, int, Specification)} should sort {@link Tool} objects by name in desc order.
     * Test receives all tool names from the database in desc order
     * Then test build sort specification for order tools by name in desc order and try to find them with {@link ToolService#findAll(int, int, Specification)}
     * Then test checks if order of tool names received from jdbcTemplate equals order of tool names from {@link ToolFilterResponse}
     * received from {@link ToolService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_desc_by_name_tools_test() {
        List<String> toolNames = jdbcTemplate.queryForList("SELECT name FROM tools_tool ORDER BY name DESC", String.class);
        Specification<Tool> spec = specBuilder(sortSpec("name,desc")).build();

        Page<ToolFilterInfo> foundedTools = service.findAll(0, 100, spec);

        List<String> resultToolNames = foundedTools.getContent().stream().map(ToolFilterInfo::name).toList();
        assertIterableEquals(toolNames, resultToolNames);
    }

    /**
     * {@link ToolService#findAll(int, int, Specification)} should sort {@link Tool} objects by createdAt in asc order.
     * Test receives all tool names from the database ordered by createdAt in asc order
     * Then test build sort specification for order tools by createdAt in asc order and try to find them with {@link ToolService#findAll(int, int, Specification)}
     * Then test checks if order of tool names received from jdbcTemplate equals order of tool names from {@link ToolFilterResponse}
     * received from {@link ToolService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_asc_by_created_date_tools_test() {
        List<String> toolNames = jdbcTemplate.queryForList("SELECT name FROM tools_tool ORDER BY created_at ASC", String.class);
        Specification<Tool> spec = specBuilder(sortSpec("createdat,asc")).build();

        Page<ToolFilterInfo> foundedTools = service.findAll(0, 100, spec);

        List<String> resultToolNames = foundedTools.getContent().stream().map(ToolFilterInfo::name).toList();
        assertIterableEquals(toolNames, resultToolNames);
    }

    /**
     * {@link ToolService#findAll(int, int, Specification)} should sort {@link Tool} objects by createdAt in desc order.
     * Test receives all tool names from the database ordered by createdAt in desc order
     * Then test build sort specification for order tools by createdAt in desc order and try to find them with {@link ToolService#findAll(int, int, Specification)}
     * Then test checks if order of tool names received from jdbcTemplate equals order of tool names from {@link ToolFilterResponse}
     * received from {@link ToolService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_desc_by_created_date_tools_test() {
        List<String> toolNames = jdbcTemplate.queryForList("SELECT name FROM tools_tool ORDER BY created_at DESC", String.class);
        Specification<Tool> spec = specBuilder(sortSpec("createdat,desc")).build();

        Page<ToolFilterInfo> foundedTools = service.findAll(0, 100, spec);

        List<String> resultToolNames = foundedTools.getContent().stream().map(ToolFilterInfo::name).toList();
        assertIterableEquals(toolNames, resultToolNames);
    }

    /**
     * {@link ToolService#findAll(int, int, Specification)} without filters, by default should sort {@link Tool} objects by createdAt in desc order.
     * Test receives all tool names from the database ordered by createdAt in desc order
     * Then test build sort specification null parameter and try to find tools with {@link ToolService#findAll(int, int, Specification)}
     * Then test checks if order of tool names received from jdbcTemplate equals order of tool names from {@link ToolFilterResponse}
     * received from {@link ToolService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_null_filter_should_sort_by_default_desc_by_created_date_tools_test() {
        List<String> toolNames = jdbcTemplate.queryForList("SELECT name FROM tools_tool ORDER BY created_at DESC", String.class);
        Specification<Tool> spec = specBuilder(sortSpec(null)).build();

        Page<ToolFilterInfo> foundedTools = service.findAll(0, 100, spec);

        List<String> resultToolNames = foundedTools.getContent().stream().map(ToolFilterInfo::name).toList();
        assertIterableEquals(toolNames, resultToolNames);
    }

    /**
     * {@link ToolService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Tool} objects by createdAt in desc order.
     * Test receives all tool names from the database ordered by createdAt in desc order
     * Then test build sort specification with empty field "  " parameter and try to find tools with {@link ToolService#findAll(int, int, Specification)}
     * Then test checks if order of tool names received from jdbcTemplate equals order of tool names from {@link ToolFilterResponse}
     * received from {@link ToolService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_blank_filter_should_sort_by_default_desc_by_created_date_tools_test() {
        List<String> toolNames = jdbcTemplate.queryForList("SELECT name FROM tools_tool ORDER BY created_at DESC", String.class);
        Specification<Tool> spec = specBuilder(sortSpec(" ")).build();

        Page<ToolFilterInfo> foundedTools = service.findAll(0, 100, spec);

        List<String> resultToolNames = foundedTools.getContent().stream().map(ToolFilterInfo::name).toList();
        assertIterableEquals(toolNames, resultToolNames);
    }

    /**
     * {@link ToolService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Tool} objects by createdAt in desc order.
     * Test receives all tool names from the database ordered by createdAt in desc order
     * Then test build sort specification with unsupported parameter and try to find tools with {@link ToolService#findAll(int, int, Specification)}
     * Then test checks if order of tool names received from jdbcTemplate equals order of tool names from {@link ToolFilterResponse}
     * received from {@link ToolService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_unsupported_filter_should_sort_by_default_desc_by_created_date_tools_test() {
        List<String> toolNames = jdbcTemplate.queryForList("SELECT name FROM tools_tool ORDER BY created_at DESC", String.class);
        Specification<Tool> spec = specBuilder(sortSpec("unsupported_filter")).build();

        Page<ToolFilterInfo> foundedTools = service.findAll(0, 100, spec);

        List<String> resultToolNames = foundedTools.getContent().stream().map(ToolFilterInfo::name).toList();
        assertIterableEquals(toolNames, resultToolNames);
    }

    /**
     * {@link ToolService#findAll(int, int, Specification)} should return tools with specified size.
     * Test creates null specification.
     * Then test with page size 1 returns {@link ToolFilterResponse} from  {@link ToolService#findAll(int, int, Specification)}
     * Then test checks total received items and concrete size of founded tools in concrete page.
     */
    @Test
    public void findAll_with_page_size_one_should_return_one_tool_test() {
        Specification<Tool> spec = specBuilder(sortSpec(null)).build();

        Page<ToolFilterInfo> foundedTools = service.findAll(0, 1, spec);

        assertEquals(6, foundedTools.getTotalElements());
        assertEquals(1, foundedTools.getContent().size());
    }
}