package tech.konso.toolsmanagement.modules.persons.business.label.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import tech.konso.toolsmanagement.modules.persons.business.label.controller.dto.LabelFilterResponse;
import tech.konso.toolsmanagement.modules.persons.business.label.controller.dto.LabelRequest;
import tech.konso.toolsmanagement.modules.persons.business.label.persistence.dao.Label;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tech.konso.toolsmanagement.modules.persons.business.label.persistence.specification.LabelSpecification.*;
import static tech.konso.toolsmanagement.modules.persons.commons.AbstractSpecification.specBuilder;

/**
 * Label service layer tests.
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
public class LabelServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LabelService service;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('label_1')");
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('label_2')");
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('label_3')");
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('label_4')");
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('label_5')");
        jdbcTemplate.update("INSERT INTO persons_label (name, is_archived) VALUES ('label_6',  'true')");

    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM persons_label");
    }

    private LabelRequest.LabelRequestBuilder getDefaultLabelRequest() {
        return LabelRequest.builder()
                .name("Important")
                .isArchived(false);
    }

    /**
     * {@link LabelService#findById(Long)} should return {@link Label} by id from database.
     * Test checks equality labelId (received from jdbcTemplate request)
     * with id of label object received from {@link LabelService#findById(Long)}
     */
    @Test
    public void findById_should_return_label_test() {
        long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = 'label_1' AND is_archived IS FALSE", Long.class);

        Label label = service.findById(labelId);

        assertEquals(labelId, label.getId());
    }

    /**
     * {@link LabelService#findById(Long)} should throw {@link BPException} exception
     * if {@link Label} with id not exist in database.
     * Test try to find label whit id = -1 (negative num,ber guaranties, that no such id exists in database)
     * and check if {@link BPException} is thrown.
     */
    @Test
    public void findById_should_throw_exception_on_not_fount_label_test() {
        long labelId = -1;

        assertThrows(BPException.class, () -> service.findById(labelId));
    }

    /**
     * {@link LabelService#save(LabelRequest)} should update {@link Label} name field.
     * Test finds existing label id in database with jdbcTemplate and try to update it name
     * using {@link LabelService#save(LabelRequest)}.
     * Then checks if name was updated or not (by compare {@link LabelRequest} name and labelName received from database).
     */
    @Test
    public void update_should_update_label_name_test() {
        long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = 'label_1' AND is_archived IS FALSE", Long.class);
        LabelRequest rq = getDefaultLabelRequest()
                .id(labelId)
                .build();

        service.save(rq);

        String labelName = jdbcTemplate.queryForObject("SELECT name FROM persons_label WHERE label_id = " + labelId + " AND is_archived IS FALSE", String.class);
        assertEquals(rq.name(), labelName);
    }

    /**
     * {@link LabelService#save(LabelRequest)} should update {@link Label} isArchived field.
     * Test finds existing label id in database with jdbcTemplate and try to update it isArchived flag
     * using {@link LabelService#save(LabelRequest)}.
     * Then checks if isArchived flag was updated or not (using assertTrue on field).
     */
    @Test
    public void update_should_archive_label_test() {
        long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = 'label_1' AND is_archived IS FALSE", Long.class);
        LabelRequest rq = getDefaultLabelRequest()
                .id(labelId)
                .isArchived(true)
                .build();

        service.save(rq);

        Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM persons_label WHERE label_id = " + labelId, Boolean.class);
        assertTrue(isArchived);
    }

    /**
     * {@link LabelService#save(LabelRequest)} should not update {@link Label} if name field is null.
     * Test finds existing label id in database with jdbcTemplate and try to update it name field
     * using {@link LabelService#save(LabelRequest)}.
     * Then checks if exception {@link DataIntegrityViolationException} was thrown.
     * Then checks if field name not changed during test.
     */
    @Test
    public void update_should_not_update_null_name_test() {
        String labelName = "label_1";
        long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = '" + labelName + "' AND is_archived IS FALSE", Long.class);
        LabelRequest rq = getDefaultLabelRequest()
                .id(labelId)
                .name(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        String labelNameFromDb = jdbcTemplate.queryForObject("SELECT name FROM persons_label WHERE label_id = " + labelId + " AND is_archived IS FALSE", String.class);
        assertEquals(labelName, labelNameFromDb);
    }

    /**
     * {@link LabelService#save(LabelRequest)} should not update {@link Label} if isArchived flag is null.
     * Test finds existing label id in database with jdbcTemplate and try to update it isArchived flag
     * using {@link LabelService#save(LabelRequest)}.
     * Then checks if exception {@link DataIntegrityViolationException} was thrown.
     * Then checks if isArchived flag not changed during test.
     */
    @Test
    public void update_should_not_update_null_isArchived_test() {
        long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = 'label_1' AND is_archived IS FALSE", Long.class);
        LabelRequest rq = getDefaultLabelRequest()
                .id(labelId)
                .isArchived(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM persons_label WHERE label_id = " + labelId, Boolean.class);
        assertFalse(isArchived);
    }

    /**
     * {@link LabelService#save(LabelRequest)}} should save {@link Label} object.
     * Test creates dto object {@link LabelRequest} and then using {@link LabelService#save(LabelRequest)}
     * try to save new {@link Label} object to database.
     * Then checks returns {@link Label} object if id not null, name field and isArchived flag equals this fields
     * from dto object {@link LabelRequest}.
     */
    @Test
    public void save_should_save_label_test() {
        LabelRequest rq = getDefaultLabelRequest().build();

        Label savedLabel = service.save(rq);

        assertNotNull(savedLabel.getId());
        assertEquals(rq.name(), savedLabel.getName());
        assertEquals(rq.isArchived(), savedLabel.getIsArchived());
    }

    /**
     * {@link LabelService#save(LabelRequest)}} should not save {@link Label} object
     * if field name already exists in database.
     * Test try to save Label with existing field name and check if {@link DataIntegrityViolationException} is thrown.
     * Then check if only one label with given name exist in database.
     */
    @Test
    public void save_should_not_save_if_label_name_already_exists_test() {
        String labelName = "label_1";
        LabelRequest rq = getDefaultLabelRequest()
                .name(labelName)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_label WHERE name = '" + labelName + "'", Long.class);
        assertEquals(1L, count);
    }

    /**
     * {@link LabelService#save(LabelRequest)}} should not save {@link Label} object if field name is null.
     * Test try to save Label with null field name and check if {@link DataIntegrityViolationException} is thrown.
     * Then test checks if there is no labels with null name exists in the database
     */
    @Test
    public void save_should_not_save_if_label_name_is_null_exists_test() {
        LabelRequest rq = getDefaultLabelRequest()
                .name(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_label WHERE name IS NULL", Long.class);
        assertEquals(0L, count);
    }

    /**
     * {@link LabelService#findAll(int, int, Specification)} should return all not archived {@link Label} objects.
     * Test counts all not archived label objects in the database using jdbcTemplate
     * Then test build isArchived specification for not archived labels and try to find them with {@link LabelService#findAll(int, int, Specification)}
     * Then test checks if counts not archived labels from jdbcTemplate equals totalItems from {@link LabelFilterResponse}
     * received from {@link LabelService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_all_not_archived_labels_test() {
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_label WHERE is_archived IS FALSE", Long.class);
        Specification<Label> spec = specBuilder(isArchivedSpec(false)).build();

        Page<Label> foundedLabels = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedLabels.getTotalElements());
    }

    /**
     * {@link LabelService#findAll(int, int, Specification)} should return all archived {@link Label} objects.
     * Test counts all archived label objects in the database using jdbcTemplate
     * Then test build isArchived specification for archived labels and try to find them with {@link LabelService#findAll(int, int, Specification)}
     * Then test checks if counts archived labels from jdbcTemplate equals totalItems from {@link LabelFilterResponse}
     * received from {@link LabelService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_all_archived_labels_test() {
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_label WHERE is_archived IS TRUE", Long.class);
        Specification<Label> spec = specBuilder(isArchivedSpec(true)).build();

        Page<Label> foundedLabels = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedLabels.getTotalElements());
    }

    /**
     * {@link LabelService#findAll(int, int, Specification)} should return {@link Label} objects by like name pattern.
     * Test counts all label objects in the database matches %ran% pattern using jdbcTemplate
     * Then test build like specification for name with %ran% pattern and try to find them with {@link LabelService#findAll(int, int, Specification)}
     * Then test checks if counts labels from jdbcTemplate equals totalItems from {@link LabelFilterResponse}
     * received from {@link LabelService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_by_name_labels_test() {
        String likeName = "lab";
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('Important')");
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('DDLABEDDD')");
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM persons_label WHERE LOWER (name) LIKE '%" + likeName + "%'", Long.class);
        Specification<Label> spec = specBuilder(likeSpec(likeName)).build();

        Page<Label> foundedLabels = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedLabels.getTotalElements());
    }

    /**
     * {@link LabelService#findAll(int, int, Specification)} should sort {@link Label} objects by name in asc order.
     * Test receives all label names from the database in asc order
     * Then test build sort specification for order labels by name in asc order and try to find them with {@link LabelService#findAll(int, int, Specification)}
     * Then test checks if order of label names received from jdbcTemplate equals order of label names from {@link LabelFilterResponse}
     * received from {@link LabelService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_asc_by_name_labels_test() {
        List<String> labelNames = jdbcTemplate.queryForList("SELECT name FROM persons_label ORDER BY name ASC", String.class);
        Specification<Label> spec = specBuilder(sortSpec("name,asc")).build();

        Page<Label> foundedLabels = service.findAll(0, 100, spec);

        List<String> resultLabelNames = foundedLabels.getContent().stream().map(Label::getName).toList();
        assertIterableEquals(labelNames, resultLabelNames);
    }

    /**
     * {@link LabelService#findAll(int, int, Specification)} should sort {@link Label} objects by name in desc order.
     * Test receives all label names from the database in desc order
     * Then test build sort specification for order labels by name in desc order and try to find them with {@link LabelService#findAll(int, int, Specification)}
     * Then test checks if order of label names received from jdbcTemplate equals order of label names from {@link LabelFilterResponse}
     * received from {@link LabelService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_desc_by_name_labels_test() {
        List<String> labelNames = jdbcTemplate.queryForList("SELECT name FROM persons_label ORDER BY name DESC", String.class);
        Specification<Label> spec = specBuilder(sortSpec("name,desc")).build();

        Page<Label> foundedLabels = service.findAll(0, 100, spec);

        List<String> resultLabelNames = foundedLabels.getContent().stream().map(Label::getName).toList();
        assertIterableEquals(labelNames, resultLabelNames);
    }

    /**
     * {@link LabelService#findAll(int, int, Specification)} should sort {@link Label} objects by createdAt in asc order.
     * Test receives all label names from the database ordered by createdAt in asc order
     * Then test build sort specification for order labels by createdAt in asc order and try to find them with {@link LabelService#findAll(int, int, Specification)}
     * Then test checks if order of label names received from jdbcTemplate equals order of label names from {@link LabelFilterResponse}
     * received from {@link LabelService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_asc_by_created_date_labels_test() {
        List<String> labelNames = jdbcTemplate.queryForList("SELECT name FROM persons_label ORDER BY created_at ASC", String.class);
        Specification<Label> spec = specBuilder(sortSpec("createdat,asc")).build();

        Page<Label> foundedLabels = service.findAll(0, 100, spec);

        List<String> resultLabelNames = foundedLabels.getContent().stream().map(Label::getName).toList();
        assertIterableEquals(labelNames, resultLabelNames);
    }

    /**
     * {@link LabelService#findAll(int, int, Specification)} should sort {@link Label} objects by createdAt in desc order.
     * Test receives all label names from the database ordered by createdAt in desc order
     * Then test build sort specification for order labels by createdAt in desc order and try to find them with {@link LabelService#findAll(int, int, Specification)}
     * Then test checks if order of label names received from jdbcTemplate equals order of label names from {@link LabelFilterResponse}
     * received from {@link LabelService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_desc_by_created_date_labels_test() {
        List<String> labelNames = jdbcTemplate.queryForList("SELECT name FROM persons_label ORDER BY created_at DESC", String.class);
        Specification<Label> spec = specBuilder(sortSpec("createdat,desc")).build();

        Page<Label> foundedLabels = service.findAll(0, 100, spec);

        List<String> resultLabelNames = foundedLabels.getContent().stream().map(Label::getName).toList();
        assertIterableEquals(labelNames, resultLabelNames);
    }

    /**
     * {@link LabelService#findAll(int, int, Specification)} without filters, by default should sort {@link Label} objects by createdAt in desc order.
     * Test receives all label names from the database ordered by createdAt in desc order
     * Then test build sort specification null parameter and try to find labels with {@link LabelService#findAll(int, int, Specification)}
     * Then test checks if order of label names received from jdbcTemplate equals order of label names from {@link LabelFilterResponse}
     * received from {@link LabelService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_null_filter_should_sort_by_default_desc_by_created_date_labels_test() {
        List<String> labelNames = jdbcTemplate.queryForList("SELECT name FROM persons_label ORDER BY created_at DESC", String.class);
        Specification<Label> spec = specBuilder(sortSpec(null)).build();

        Page<Label> foundedLabels = service.findAll(0, 100, spec);

        List<String> resultLabelNames = foundedLabels.getContent().stream().map(Label::getName).toList();
        assertIterableEquals(labelNames, resultLabelNames);
    }

    /**
     * {@link LabelService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Label} objects by createdAt in desc order.
     * Test receives all label names from the database ordered by createdAt in desc order
     * Then test build sort specification with empty field "  " parameter and try to find labels with {@link LabelService#findAll(int, int, Specification)}
     * Then test checks if order of label names received from jdbcTemplate equals order of label names from {@link LabelFilterResponse}
     * received from {@link LabelService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_blank_filter_should_sort_by_default_desc_by_created_date_labels_test() {
        List<String> labelNames = jdbcTemplate.queryForList("SELECT name FROM persons_label ORDER BY created_at DESC", String.class);
        Specification<Label> spec = specBuilder(sortSpec(" ")).build();

        Page<Label> foundedLabels = service.findAll(0, 100, spec);

        List<String> resultLabelNames = foundedLabels.getContent().stream().map(Label::getName).toList();
        assertIterableEquals(labelNames, resultLabelNames);
    }

    /**
     * {@link LabelService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Label} objects by createdAt in desc order.
     * Test receives all label names from the database ordered by createdAt in desc order
     * Then test build sort specification with unsupported parameter and try to find labels with {@link LabelService#findAll(int, int, Specification)}
     * Then test checks if order of label names received from jdbcTemplate equals order of label names from {@link LabelFilterResponse}
     * received from {@link LabelService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_unsupported_filter_should_sort_by_default_desc_by_created_date_labels_test() {
        List<String> labelNames = jdbcTemplate.queryForList("SELECT name FROM persons_label ORDER BY created_at DESC", String.class);
        Specification<Label> spec = specBuilder(sortSpec("unsupported_filter")).build();

        Page<Label> foundedLabels = service.findAll(0, 100, spec);

        List<String> resultLabelNames = foundedLabels.getContent().stream().map(Label::getName).toList();
        assertIterableEquals(labelNames, resultLabelNames);
    }

    /**
     * {@link LabelService#findAll(int, int, Specification)} should return labels with specified size.
     * Test creates null specification.
     * Then test with page size 1 returns {@link LabelFilterResponse} from  {@link LabelService#findAll(int, int, Specification)}
     * Then test checks total received items and concrete size of founded labels in concrete page.
     */
    @Test
    public void findAll_with_page_size_one_should_return_one_label_test() {
        Specification<Label> spec = specBuilder(sortSpec(null)).build();

        Page<Label> foundedLabels = service.findAll(0, 1, spec);

        assertEquals(6, foundedLabels.getTotalElements());
        assertEquals(1, foundedLabels.getContent().size());
    }
}
