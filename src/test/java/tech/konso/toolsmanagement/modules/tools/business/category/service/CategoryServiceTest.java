package tech.konso.toolsmanagement.modules.tools.business.category.service;

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
import tech.konso.toolsmanagement.modules.tools.business.category.controller.dto.CategoryFilterResponse;
import tech.konso.toolsmanagement.modules.tools.business.category.controller.dto.CategoryRequest;
import tech.konso.toolsmanagement.modules.tools.business.category.persistence.dao.Category;
import tech.konso.toolsmanagement.modules.tools.commons.exceptions.BPException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tech.konso.toolsmanagement.modules.tools.business.category.persistence.specification.CategorySpecification.*;
import static tech.konso.toolsmanagement.modules.tools.commons.AbstractSpecification.specBuilder;

/**
 * Category service layer tests.
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
public class CategoryServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CategoryService service;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('category_1')");
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('category_2')");
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('category_3')");
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('category_4')");
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('category_5')");
        jdbcTemplate.update("INSERT INTO tools_category (name, is_archived) VALUES ('category_6',  'true')");

    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM tools_category");
    }

    /**
     * {@link CategoryService#findById(Long)} should return {@link Category} by id from database.
     * Test checks equality categoryId (received from jdbcTemplate request)
     * with id of category object received from {@link CategoryService#findById(Long)}
     */
    @Test
    public void findById_should_return_category_test() {
        long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1' AND is_archived IS FALSE", Long.class);

        Category category = service.findById(categoryId);

        assertEquals(categoryId, category.getId());
    }

    /**
     * {@link CategoryService#findById(Long)} should throw {@link BPException} exception
     * if {@link Category} with id not exist in database.
     * Test try to find category whit id = -1 (negative num,ber guaranties, that no such id exists in database)
     * and check if {@link BPException} is thrown.
     */
    @Test
    public void findById_should_throw_exception_on_not_fount_category_test() {
        long categoryId = -1;

        assertThrows(BPException.class, () -> service.findById(categoryId));
    }

    /**
     * {@link CategoryService#update(Long, CategoryRequest)} should update {@link Category} name field.
     * Test finds existing category id in database with jdbcTemplate and try to update it name
     * using {@link CategoryService#update(Long, CategoryRequest)}.
     * Then checks if name was updated or not (by compare {@link CategoryRequest} name and categoryName received from database).
     */
    @Test
    public void update_should_update_category_name_test() {
        long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1' AND is_archived IS FALSE", Long.class);
        CategoryRequest rq = new CategoryRequest("new_category", false);

        service.update(categoryId, rq);

        String categoryName = jdbcTemplate.queryForObject("SELECT name FROM tools_category WHERE category_id = " + categoryId + " AND is_archived IS FALSE", String.class);
        assertEquals(rq.name(), categoryName);
    }

    /**
     * {@link CategoryService#update(Long, CategoryRequest)} should update {@link Category} isArchived field.
     * Test finds existing category id in database with jdbcTemplate and try to update it isArchived flag
     * using {@link CategoryService#update(Long, CategoryRequest)}.
     * Then checks if isArchived flag was updated or not (using assertTrue on field).
     */
    @Test
    public void update_should_archive_category_test() {
        long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1' AND is_archived IS FALSE", Long.class);
        CategoryRequest rq = new CategoryRequest("new_category", true);

        service.update(categoryId, rq);

        Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM tools_category WHERE category_id = " + categoryId, Boolean.class);
        assertTrue(isArchived);
    }

    /**
     * {@link CategoryService#update(Long, CategoryRequest)} should not update {@link Category} if name field is null.
     * Test finds existing category id in database with jdbcTemplate and try to update it name field
     * using {@link CategoryService#update(Long, CategoryRequest)}.
     * Then checks if exception {@link DataIntegrityViolationException} was thrown.
     * Then checks if field name not changed during test.
     */
    @Test
    public void update_should_not_update_null_name_test() {
        String categoryName = "category_1";
        long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = '" + categoryName + "' AND is_archived IS FALSE", Long.class);
        CategoryRequest rq = new CategoryRequest(null, false);

        assertThrows(DataIntegrityViolationException.class, () -> service.update(categoryId, rq));

        String categoryNameFromDb = jdbcTemplate.queryForObject("SELECT name FROM tools_category WHERE category_id = " + categoryId + " AND is_archived IS FALSE", String.class);
        assertEquals(categoryName, categoryNameFromDb);
    }

    /**
     * {@link CategoryService#update(Long, CategoryRequest)} should not update {@link Category} if isArchived flag is null.
     * Test finds existing category id in database with jdbcTemplate and try to update it isArchived flag
     * using {@link CategoryService#update(Long, CategoryRequest)}.
     * Then checks if exception {@link DataIntegrityViolationException} was thrown.
     * Then checks if isArchived flag not changed during test.
     */
    @Test
    public void update_should_not_update_null_isArchived_test() {
        long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1' AND is_archived IS FALSE", Long.class);
        CategoryRequest rq = new CategoryRequest("new_category", null);

        assertThrows(DataIntegrityViolationException.class, () -> service.update(categoryId, rq));

        Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM tools_category WHERE category_id = " + categoryId, Boolean.class);
        assertFalse(isArchived);
    }

    /**
     * {@link CategoryService#save(CategoryRequest)}} should save {@link Category} object.
     * Test creates dto object {@link CategoryRequest} and then using {@link CategoryService#save(CategoryRequest)}
     * try to save new {@link Category} object to database.
     * Then checks returns {@link Category} object if id not null, name field and isArchived flag equals this fields
     * from dto object {@link CategoryRequest}.
     */
    @Test
    public void save_should_save_category_test() {
        CategoryRequest rq = new CategoryRequest("new_category", false);

        Category savedCategory = service.save(rq);

        assertNotNull(savedCategory.getId());
        assertEquals(rq.name(), savedCategory.getName());
        assertEquals(rq.isArchived(), savedCategory.getIsArchived());
    }

    /**
     * {@link CategoryService#save(CategoryRequest)}} should not save {@link Category} object
     * if field name already exists in database.
     * Test try to save Category with existing field name and check if {@link DataIntegrityViolationException} is thrown.
     * Then check if only one category with given name exist in database.
     */
    @Test
    public void save_should_not_save_if_category_name_already_exists_test() {
        String categoryName = "category_1";
        CategoryRequest rq = new CategoryRequest(categoryName, false);

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_category WHERE name = '" + categoryName + "'", Long.class);
        assertEquals(1L, count);
    }

    /**
     * {@link CategoryService#save(CategoryRequest)}} should not save {@link Category} object if field name is null.
     * Test try to save Category with null field name and check if {@link DataIntegrityViolationException} is thrown.
     * Then test checks if there is no categories with null name exists in the database
     */
    @Test
    public void save_should_not_save_if_category_name_is_null_exists_test() {
        CategoryRequest rq = new CategoryRequest(null, false);

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_category WHERE name IS NULL", Long.class);
        assertEquals(0L, count);
    }

    /**
     * {@link CategoryService#findAll(int, int, Specification)} should return all not archived {@link Category} objects.
     * Test counts all not archived category objects in the database using jdbcTemplate
     * Then test build isArchived specification for not archived categories and try to find them with {@link CategoryService#findAll(int, int, Specification)}
     * Then test checks if counts not archived categories from jdbcTemplate equals totalItems from {@link CategoryFilterResponse}
     * received from {@link CategoryService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_all_not_archived_categories_test() {
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_category WHERE is_archived IS FALSE", Long.class);
        Specification<Category> spec = specBuilder(isArchivedSpec(false)).build();

        Page<Category> foundedCategories = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedCategories.getTotalElements());
    }

    /**
     * {@link CategoryService#findAll(int, int, Specification)} should return all archived {@link Category} objects.
     * Test counts all archived category objects in the database using jdbcTemplate
     * Then test build isArchived specification for archived categories and try to find them with {@link CategoryService#findAll(int, int, Specification)}
     * Then test checks if counts archived categories from jdbcTemplate equals totalItems from {@link CategoryFilterResponse}
     * received from {@link CategoryService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_all_archived_categories_test() {
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_category WHERE is_archived IS TRUE", Long.class);
        Specification<Category> spec = specBuilder(isArchivedSpec(true)).build();

        Page<Category> foundedCategories = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedCategories.getTotalElements());
    }

    /**
     * {@link CategoryService#findAll(int, int, Specification)} should return {@link Category} objects by like name pattern.
     * Test counts all category objects in the database matches %cat% pattern using jdbcTemplate
     * Then test build like specification for name with %cat% pattern and try to find them with {@link CategoryService#findAll(int, int, Specification)}
     * Then test checks if counts categories from jdbcTemplate equals totalItems from {@link CategoryFilterResponse}
     * received from {@link CategoryService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_by_name_categories_test() {
        String likeName = "cat";
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('HandTool')");
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('DDCATEDD')");
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_category WHERE LOWER (name) LIKE '%" + likeName + "%'", Long.class);
        Specification<Category> spec = specBuilder(likeSpec(likeName)).build();

        Page<Category> foundedCategories = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedCategories.getTotalElements());
    }

    /**
     * {@link CategoryService#findAll(int, int, Specification)} should sort {@link Category} objects by name in asc order.
     * Test receives all category names from the database in asc order
     * Then test build sort specification for order categories by name in asc order and try to find them with {@link CategoryService#findAll(int, int, Specification)}
     * Then test checks if order of category names received from jdbcTemplate equals order of category names from {@link CategoryFilterResponse}
     * received from {@link CategoryService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_asc_by_name_categories_test() {
        List<String> categoryNames = jdbcTemplate.queryForList("SELECT name FROM tools_category ORDER BY name ASC", String.class);
        Specification<Category> spec = specBuilder(sortSpec("name,asc")).build();

        Page<Category> foundedCategories = service.findAll(0, 100, spec);

        List<String> resultCategoryNames = foundedCategories.getContent().stream().map(Category::getName).toList();
        assertIterableEquals(categoryNames, resultCategoryNames);
    }

    /**
     * {@link CategoryService#findAll(int, int, Specification)} should sort {@link Category} objects by name in desc order.
     * Test receives all category names from the database in desc order
     * Then test build sort specification for order categories by name in desc order and try to find them with {@link CategoryService#findAll(int, int, Specification)}
     * Then test checks if order of category names received from jdbcTemplate equals order of category names from {@link CategoryFilterResponse}
     * received from {@link CategoryService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_desc_by_name_categories_test() {
        List<String> categoryNames = jdbcTemplate.queryForList("SELECT name FROM tools_category ORDER BY name DESC", String.class);
        Specification<Category> spec = specBuilder(sortSpec("name,desc")).build();

        Page<Category> foundedCategories = service.findAll(0, 100, spec);

        List<String> resultCategoryNames = foundedCategories.getContent().stream().map(Category::getName).toList();
        assertIterableEquals(categoryNames, resultCategoryNames);
    }

    /**
     * {@link CategoryService#findAll(int, int, Specification)} should sort {@link Category} objects by createdAt in asc order.
     * Test receives all category names from the database ordered by createdAt in asc order
     * Then test build sort specification for order categories by createdAt in asc order and try to find them with {@link CategoryService#findAll(int, int, Specification)}
     * Then test checks if order of category names received from jdbcTemplate equals order of category names from {@link CategoryFilterResponse}
     * received from {@link CategoryService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_asc_by_created_date_categories_test() {
        List<String> categoryNames = jdbcTemplate.queryForList("SELECT name FROM tools_category ORDER BY created_at ASC", String.class);
        Specification<Category> spec = specBuilder(sortSpec("createdat,asc")).build();

        Page<Category> foundedCategories = service.findAll(0, 100, spec);

        List<String> resultCategoryNames = foundedCategories.getContent().stream().map(Category::getName).toList();
        assertIterableEquals(categoryNames, resultCategoryNames);
    }

    /**
     * {@link CategoryService#findAll(int, int, Specification)} should sort {@link Category} objects by createdAt in desc order.
     * Test receives all category names from the database ordered by createdAt in desc order
     * Then test build sort specification for order categories by createdAt in desc order and try to find them with {@link CategoryService#findAll(int, int, Specification)}
     * Then test checks if order of category names received from jdbcTemplate equals order of category names from {@link CategoryFilterResponse}
     * received from {@link CategoryService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_desc_by_created_date_categories_test() {
        List<String> categoryNames = jdbcTemplate.queryForList("SELECT name FROM tools_category ORDER BY created_at DESC", String.class);
        Specification<Category> spec = specBuilder(sortSpec("createdat,desc")).build();

        Page<Category> foundedCategories = service.findAll(0, 100, spec);

        List<String> resultCategoryNames = foundedCategories.getContent().stream().map(Category::getName).toList();
        assertIterableEquals(categoryNames, resultCategoryNames);
    }

    /**
     * {@link CategoryService#findAll(int, int, Specification)} without filters, by default should sort {@link Category} objects by createdAt in desc order.
     * Test receives all category names from the database ordered by createdAt in desc order
     * Then test build sort specification null parameter and try to find categories with {@link CategoryService#findAll(int, int, Specification)}
     * Then test checks if order of category names received from jdbcTemplate equals order of category names from {@link CategoryFilterResponse}
     * received from {@link CategoryService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_null_filter_should_sort_by_default_desc_by_created_date_categories_test() {
        List<String> categoryNames = jdbcTemplate.queryForList("SELECT name FROM tools_category ORDER BY created_at DESC", String.class);
        Specification<Category> spec = specBuilder(sortSpec(null)).build();

        Page<Category> foundedCategories = service.findAll(0, 100, spec);

        List<String> resultCategoryNames = foundedCategories.getContent().stream().map(Category::getName).toList();
        assertIterableEquals(categoryNames, resultCategoryNames);
    }

    /**
     * {@link CategoryService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Category} objects by createdAt in desc order.
     * Test receives all category names from the database ordered by createdAt in desc order
     * Then test build sort specification with empty field "  " parameter and try to find categories with {@link CategoryService#findAll(int, int, Specification)}
     * Then test checks if order of category names received from jdbcTemplate equals order of category names from {@link CategoryFilterResponse}
     * received from {@link CategoryService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_blank_filter_should_sort_by_default_desc_by_created_date_categories_test() {
        List<String> categoryNames = jdbcTemplate.queryForList("SELECT name FROM tools_category ORDER BY created_at DESC", String.class);
        Specification<Category> spec = specBuilder(sortSpec(" ")).build();

        Page<Category> foundedCategories = service.findAll(0, 100, spec);

        List<String> resultCategoryNames = foundedCategories.getContent().stream().map(Category::getName).toList();
        assertIterableEquals(categoryNames, resultCategoryNames);
    }

    /**
     * {@link CategoryService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Category} objects by createdAt in desc order.
     * Test receives all category names from the database ordered by createdAt in desc order
     * Then test build sort specification with unsupported parameter and try to find categories with {@link CategoryService#findAll(int, int, Specification)}
     * Then test checks if order of category names received from jdbcTemplate equals order of category names from {@link CategoryFilterResponse}
     * received from {@link CategoryService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_unsupported_filter_should_sort_by_default_desc_by_created_date_categories_test() {
        List<String> categoryNames = jdbcTemplate.queryForList("SELECT name FROM tools_category ORDER BY created_at DESC", String.class);
        Specification<Category> spec = specBuilder(sortSpec("unsupported_filter")).build();

        Page<Category> foundedCategories = service.findAll(0, 100, spec);

        List<String> resultCategoryNames = foundedCategories.getContent().stream().map(Category::getName).toList();
        assertIterableEquals(categoryNames, resultCategoryNames);
    }

    /**
     * {@link CategoryService#findAll(int, int, Specification)} should return categories with specified size.
     * Test creates null specification.
     * Then test with page size 1 returns {@link CategoryFilterResponse} from  {@link CategoryService#findAll(int, int, Specification)}
     * Then test checks total received items and concrete size of founded categories in concrete page.
     */
    @Test
    public void findAll_with_page_size_one_should_return_one_category_test() {
        Specification<Category> spec = specBuilder(sortSpec(null)).build();

        Page<Category> foundedCategories = service.findAll(0, 1, spec);

        assertEquals(6, foundedCategories.getTotalElements());
        assertEquals(1, foundedCategories.getContent().size());
    }
}