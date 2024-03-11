package tech.konso.toolsmanagement.modules.business.persons.label.service.mappers;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tech.konso.toolsmanagement.modules.business.persons.label.controller.dto.LabelRequest;
import tech.konso.toolsmanagement.modules.business.persons.label.persistence.dao.Label;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for LabelEntityMapper. Test for mapping fields and null values.
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
public class LabelEntityMapperTest {

    private LabelEntityMapper mapper;

    @BeforeAll
    public void init() {
        mapper = new LabelEntityMapper();
    }

    /**
     * {@link LabelEntityMapper#toEntity(Label, LabelRequest)} should map {@link Label} name field.
     * Test creates object {@link LabelRequest} with non-null test field and try to map it to {@link Label} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_label_should_map_name() {
        String labelName = "name";
        LabelRequest rq = LabelRequest.builder()
                .name(labelName)
                .build();

        Label label = mapper.toEntity(new Label(), rq);

        assertEquals(labelName, label.getName());
    }

    /**
     * {@link LabelEntityMapper#toEntity(Label, LabelRequest)} should map {@link Label} isArchived field.
     * Test creates object {@link LabelRequest} with non-null test field and try to map it to {@link Label} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_label_should_map_is_archived() {
        boolean isArchived = true;
        LabelRequest rq = LabelRequest.builder()
                .isArchived(isArchived)
                .build();

        Label label = mapper.toEntity(new Label(), rq);

        assertEquals(isArchived, label.getIsArchived());
    }
}

