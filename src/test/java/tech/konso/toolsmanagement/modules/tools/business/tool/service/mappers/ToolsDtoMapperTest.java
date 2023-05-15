package tech.konso.toolsmanagement.modules.tools.business.tool.service.mappers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tech.konso.toolsmanagement.modules.tools.business.brand.persistence.dao.Brand;
import tech.konso.toolsmanagement.modules.tools.business.category.persistence.dao.Category;
import tech.konso.toolsmanagement.modules.tools.business.label.persistence.dao.Label;
import tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto.LabelShort;
import tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto.ToolFilterInfo;
import tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto.ToolInfo;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.dao.Tool;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.dao.enums.OwnershipType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ToolsDtoMapper. Test for mapping fields and null values.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ToolsDtoMapperTest {
    private ToolsDtoMapper mapper;

    @BeforeAll
    public void init() {
        mapper = new ToolsDtoMapper();
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} uuid field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_uuid() {
        UUID uuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
        Tool tool = new Tool();
        tool.setUuid(uuid);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(uuid, toolFilterInfo.uuid());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} name field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_name() {
        String toolNme = "new_tool";
        Tool tool = new Tool();
        tool.setName(toolNme);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(toolNme, toolFilterInfo.name());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} IsConsumable field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_is_consumable() {
        Boolean isConsumable = true;
        Tool tool = new Tool();
        tool.setIsConsumable(isConsumable);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(isConsumable, toolFilterInfo.isConsumable());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} brand field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_brand() {
        String brandName = "new_brand";
        Brand brand = new Brand();
        brand.setName(brandName);
        Tool tool = new Tool();
        tool.setBrand(brand);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(brandName, toolFilterInfo.brand());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} null brand field.
     * Test creates object {@link Tool} with null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_null_brand() {
        Tool tool = new Tool();
        tool.setBrand(null);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertNull(toolFilterInfo.brand());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} inventoryNumber field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_inventory_number() {
        String inventoryNumber = "inventory_number";
        Tool tool = new Tool();
        tool.setInventoryNumber(inventoryNumber);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(inventoryNumber, toolFilterInfo.inventoryNumber());
    }

    //TODO: enable and change test logic after adding persons module
    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} responsibleUuid field to responsible name.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field it should contain responsible person name.
     */
    @Test
    @Disabled
    public void map_to_tool_filter_info_should_map_responsible() {
        UUID responsible = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
        Tool tool = new Tool();
        tool.setResponsibleUuid(responsible);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(responsible.toString(), toolFilterInfo.responsible());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} category field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_category() {
        String categoryName = "new_category";
        Category category = new Category();
        category.setName(categoryName);
        Tool tool = new Tool();
        tool.setCategory(category);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(categoryName, toolFilterInfo.category());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} null category field.
     * Test creates object {@link Tool} with null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_null_category() {
        Tool tool = new Tool();
        tool.setCategory(null);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertNull(toolFilterInfo.category());
    }

    //TODO: enable and change test logic after adding projects module
    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} projectUuid field to project name.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field it should contain project name.
     */
    @Test
    @Disabled
    public void map_to_tool_filter_info_should_map_project() {
        UUID responsible = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
        Tool tool = new Tool();
        tool.setResponsibleUuid(responsible);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(responsible.toString(), toolFilterInfo.responsible());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} price field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_price() {
        BigDecimal price = new BigDecimal("23400.00");
        Tool tool = new Tool();
        tool.setPrice(price);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(price, toolFilterInfo.price());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} ownershipType field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_ownership_type() {
        OwnershipType ownershipType = OwnershipType.RENT;
        Tool tool = new Tool();
        tool.setOwnershipType(ownershipType);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(ownershipType, toolFilterInfo.ownershipType());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} rentTill field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_rent_till() {
        LocalDate rentTill = LocalDate.of(2023, Month.MAY, 10);
        Tool tool = new Tool();
        tool.setRentTill(rentTill);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(rentTill, toolFilterInfo.rentTill());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} isKit field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_is_kit() {
        Boolean isKit = true;
        Tool tool = new Tool();
        tool.setIsKit(isKit);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(isKit, toolFilterInfo.isKit());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} kitUuid field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_kit_uuid() {
        UUID kitUuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
        Tool tool = new Tool();
        tool.setKitUuid(kitUuid);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(kitUuid, toolFilterInfo.kitUuid());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} labels field to set of label names.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field it should contain label names.
     */
    @Test
    public void map_to_tool_filter_info_should_map_labels() {
        String labelName1 = "new_label_1";
        String labelName2 = "new_label_2";
        Label label1 = new Label();
        Label label2 = new Label();
        label1.setName(labelName1);
        label2.setName(labelName2);
        Tool tool = new Tool();
        tool.addLabel(label1);
        tool.addLabel(label2);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertIterableEquals(Stream.of(labelName1, labelName2).sorted().collect(Collectors.toSet()),
                toolFilterInfo.labels().stream().sorted().collect(Collectors.toSet()));
    }

    /**
     * {@link ToolsDtoMapper#mapToToolFilterInfo(Tool)} should map {@link Tool} isArchived field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_filter_info_should_map_is_archived() {
        Boolean isArchived = true;
        Tool tool = new Tool();
        tool.setIsArchived(isArchived);

        ToolFilterInfo toolFilterInfo = mapper.mapToToolFilterInfo(tool);

        assertEquals(isArchived, toolFilterInfo.isArchived());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} uuid field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_uuid() {
        UUID uuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
        Tool tool = new Tool();
        tool.setUuid(uuid);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(uuid, toolInfo.uuid());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} name field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_name() {
        String toolNme = "new_tool";
        Tool tool = new Tool();
        tool.setName(toolNme);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(toolNme, toolInfo.name());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} IsConsumable field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_is_consumable() {
        Boolean isConsumable = true;
        Tool tool = new Tool();
        tool.setIsConsumable(isConsumable);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(isConsumable, toolInfo.isConsumable());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} brand field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_brand() {
        String brandName = "new_brand";
        Brand brand = new Brand();
        brand.setName(brandName);
        Tool tool = new Tool();
        tool.setBrand(brand);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(brandName, toolInfo.brand().name());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} null brand field.
     * Test creates object {@link Tool} with null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_null_brand() {
        Tool tool = new Tool();
        tool.setBrand(null);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertNull(toolInfo.brand());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} inventoryNumber field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_inventory_number() {
        String inventoryNumber = "inventory_number";
        Tool tool = new Tool();
        tool.setInventoryNumber(inventoryNumber);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(inventoryNumber, toolInfo.inventoryNumber());
    }

    //TODO: enable and change test logic after adding persons module
    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} responsibleUuid field to responsible name.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field it should contain responsible person name.
     */
    @Test
    @Disabled
    public void map_to_tool_info_should_map_responsible() {
        UUID responsible = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
        Tool tool = new Tool();
        tool.setResponsibleUuid(responsible);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(responsible.toString(), toolInfo.responsible());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} category field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_category() {
        String categoryName = "new_category";
        Category category = new Category();
        category.setName(categoryName);
        Tool tool = new Tool();
        tool.setCategory(category);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(categoryName, toolInfo.category().name());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} null category field.
     * Test creates object {@link Tool} with null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_null_category() {
        Tool tool = new Tool();
        tool.setCategory(null);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertNull(toolInfo.category());
    }

    //TODO: enable and change test logic after adding projects module
    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} projectUuid field to project name.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field it should contain project name.
     */
    @Test
    @Disabled
    public void map_to_tool_info_should_map_project() {
        UUID responsible = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
        Tool tool = new Tool();
        tool.setResponsibleUuid(responsible);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(responsible.toString(), toolInfo.responsible());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} price field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_price() {
        BigDecimal price = new BigDecimal("23400.00");
        Tool tool = new Tool();
        tool.setPrice(price);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(price, toolInfo.price());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} ownershipType field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_ownership_type() {
        OwnershipType ownershipType = OwnershipType.RENT;
        Tool tool = new Tool();
        tool.setOwnershipType(ownershipType);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(ownershipType, toolInfo.ownershipType());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} rentTill field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_rent_till() {
        LocalDate rentTill = LocalDate.of(2023, Month.MAY, 10);
        Tool tool = new Tool();
        tool.setRentTill(rentTill);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(rentTill, toolInfo.rentTill());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} isKit field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_is_kit() {
        Boolean isKit = true;
        Tool tool = new Tool();
        tool.setIsKit(isKit);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(isKit, toolInfo.isKit());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} kitUuid field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_kit_uuid() {
        UUID kitUuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
        Tool tool = new Tool();
        tool.setKitUuid(kitUuid);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(kitUuid, toolInfo.kitUuid());
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} labels field to set of label names.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field it should contain label names.
     */
    @Test
    public void map_to_tool_info_should_map_labels() {
        String labelName1 = "new_label_1";
        String labelName2 = "new_label_2";
        Label label1 = new Label();
        Label label2 = new Label();
        label1.setName(labelName1);
        label2.setName(labelName2);
        Tool tool = new Tool();
        tool.addLabel(label1);
        tool.addLabel(label2);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertIterableEquals(Stream.of(labelName1, labelName2).sorted().collect(Collectors.toSet()),
                toolInfo.labels().stream().map(LabelShort::name).sorted().collect(Collectors.toSet()));
    }

    /**
     * {@link ToolsDtoMapper#mapToToolInfo(Tool)} should map {@link Tool} isArchived field.
     * Test creates object {@link Tool} with non-null test field and try to map it to {@link ToolInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_tool_info_should_map_is_archived() {
        Boolean isArchived = true;
        Tool tool = new Tool();
        tool.setIsArchived(isArchived);

        ToolInfo toolInfo = mapper.mapToToolInfo(tool);

        assertEquals(isArchived, toolInfo.isArchived());
    }

}
