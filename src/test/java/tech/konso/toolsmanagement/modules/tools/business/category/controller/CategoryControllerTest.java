package tech.konso.toolsmanagement.modules.tools.business.category.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import tech.konso.toolsmanagement.modules.tools.business.category.controller.dto.CategoryFilterResponse;
import tech.konso.toolsmanagement.modules.tools.business.category.controller.dto.CategoryRequest;
import tech.konso.toolsmanagement.modules.tools.business.category.persistence.dao.Category;
import tech.konso.toolsmanagement.modules.tools.business.category.service.CategoryService;
import tech.konso.toolsmanagement.modules.tools.commons.AbstractControllerTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Category controller layer tests.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */
public class CategoryControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;


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

    private String urlEndpoint() {
        return url + "/v1/tools/categories";
    }


    /**
     * {@link CategoryController#find(Long)} should return {@link Category} by id from database.
     * Test checks status code 200 and equality categoryId (received from jdbcTemplate request)
     * with id of category object received from {@link CategoryService#findById(Long)} and name.
     */
    @Test
    public void find_should_return_category_test() throws Exception {
        long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1' AND is_archived IS FALSE", Long.class);

        mockMvc.perform(get(urlEndpoint() + "/" + categoryId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        Category.class,
                        dto -> dto.getId() == categoryId && dto.getName().equals("category_1")
                )));
    }

    /**
     * {@link CategoryController#find(Long)} should return bad request if {@link Category} with id not exist in database.
     * Test try to find category whit id = -1 (negative number guaranties, that no such id exists in database)
     * and check if controller return bad request with detailed error message in header.
     */
    @Test
    public void find_should_return_bad_request_test() throws Exception {
        long categoryId = -1;

        mockMvc.perform(get(urlEndpoint() + "/" + categoryId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().stringValues("detail", "Category not found id: " + categoryId));
    }

    /**
     * {@link CategoryController#findAll(int, int, String, Boolean, String)}
     * should return bad request with incorrect page number. Test try to search all categories whit page = 0
     * and check if controller return bad request with detailed error message in header.
     */
    @Test
    public void findAll_with_incorrect_page_should_return_bab_request_test() throws Exception {
        String tail = "?page=0&size=20";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().stringValues("detail", "findAll.page: must be greater than or equal to 1"));
    }

    /**
     * {@link CategoryController#findAll(int, int, String, Boolean, String)}
     * should return bad request with incorrect size number. Test try to search all categories whit size = 99999
     * and check if controller return bad request with detailed error message in header.
     */
    @Test
    public void findAll_with_incorrect_size_should_return_bab_request_test() throws Exception {
        String tail = "?page=1&size=99999";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isBadRequest()).
                andExpect(header().stringValues("detail", "findAll.size: must be less than or equal to 50"));
    }

    /**
     * {@link CategoryController#findAll(int, int, String, Boolean, String)}  without filters should return all not archived categories.
     * Test counts all not archived categories from the database.
     * Then test make request to find all categories and checks if it returns the same number, as plane jdbc request.
     */
    @Test
    public void findAll_should_return_categories_without_filters_test() throws Exception {
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_category WHERE is_archived IS FALSE", Long.class);
        String tail = "?page=1&size=20";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        CategoryFilterResponse.class,
                        dto -> dto.totalItems() == count)
                ));
    }

    /**
     * {@link CategoryController#findAll(int, int, String, Boolean, String)}  with archived filter,
     * should return all archived categories.
     * Test counts all archived categories from the database.
     * Then test make request to find all categories and checks if it returns the same number, as plane jdbc request.
     */
    @Test
    public void findAll_should_filter_archived_categories_test() throws Exception {
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_category WHERE is_archived IS TRUE", Long.class);
        String tail = "?page=1&size=20&isArchived=true";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        CategoryFilterResponse.class,
                        dto -> dto.totalItems() == count)
                ));
    }

    /**
     * {@link CategoryController#findAll(int, int, String, Boolean, String)}  with not archived filter,
     * should return all not archived categories.
     * Test counts all not archived categories from the database.
     * Then test make request to find all categories and checks if it returns the same number, as plane jdbc request.
     */
    @Test
    public void findAll_should_filter_not_archived_categories_test() throws Exception {
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_category WHERE is_archived IS FALSE", Long.class);
        String tail = "?page=1&size=20&isArchived=false";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        CategoryFilterResponse.class,
                        dto -> dto.totalItems() == count)
                ));
    }

    /**
     * {@link CategoryController#findAll(int, int, String, Boolean, String)}  with filter by full name,
     * should return category with this name.
     * Test counts all category with concrete name.
     * Then test make request to find all categories and checks if it returns the same number, as plane jdbc request,
     * and checks if returns name equals with predefined name.
     */
    @Test
    public void findAll_should_filter_full_name_categories_test() throws Exception {
        String categoryName = "category_1";
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_category WHERE name = '" + categoryName + "' AND is_archived IS FALSE", Long.class);
        String tail = "?page=1&size=20&name=" + categoryName;

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        CategoryFilterResponse.class,
                        dto -> dto.totalItems() == count && dto.categories().get(0).getName().equals(categoryName))
                ));
    }

    /**
     * {@link CategoryController#findAll(int, int, String, Boolean, String)}  with filter by part of name,
     * should return category with this name.
     * Test counts all category with like filter by name.
     * Then test make request to find all categories and checks if it returns the same number, as plane jdbc request.
     */
    @Test
    public void findAll_should_filter_like_name_categories_test() throws Exception {
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('HandTool')");
        jdbcTemplate.update("INSERT INTO tools_category (name) VALUES ('DDCATEGDDD')");
        String categoryName = "cate";
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_category WHERE LOWER (name) LIKE '%" + categoryName + "%' AND is_archived IS FALSE", Long.class);
        String tail = "?page=1&size=20&name=" + categoryName;

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        CategoryFilterResponse.class,
                        dto -> dto.totalItems() == count)
                ));
    }

    /**
     * {@link CategoryController#findAll(int, int, String, Boolean, String)}  with filter sorting by name asc,
     * should return sorted categories by names asc.
     * Test returns category names from database(using jdbcTemplate) ordered by name asc.
     * Then test make request to find all categories and checks if it returns the categories in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_name_asc_categories_test() throws Exception {
        List<String> categoryNames = jdbcTemplate.queryForList("SELECT name FROM tools_category WHERE is_archived IS FALSE ORDER BY name ASC", String.class);
        String tail = "?page=1&size=20&sort=name,asc";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        CategoryFilterResponse.class,
                        dto -> {
                            List<String> categoryNamesResponse = dto.categories().stream().map(Category::getName).toList();
                            assertIterableEquals(categoryNames, categoryNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link CategoryController#findAll(int, int, String, Boolean, String)}  with filter sorting by name desc,
     * should return sorted categories by name desc.
     * Test returns category names from database(using jdbcTemplate) ordered by name desc.
     * Then test make request to find all categories and checks if it returns the categories in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_name_desc_categories_test() throws Exception {
        List<String> categoryNames = jdbcTemplate.queryForList("SELECT name FROM tools_category WHERE is_archived IS FALSE ORDER BY name DESC", String.class);
        String tail = "?page=1&size=20&sort=name,desc";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        CategoryFilterResponse.class,
                        dto -> {
                            List<String> categoryNamesResponse = dto.categories().stream().map(Category::getName).toList();
                            assertIterableEquals(categoryNames, categoryNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link CategoryController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date asc,
     * should return sorted categories by create date asc.
     * Test returns category names from database(using jdbcTemplate) ordered by created date asc.
     * Then test make request to find all categories and checks if it returns the categories in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_create_date_asc_categories_test() throws Exception {
        List<String> categoryNames = jdbcTemplate.queryForList("SELECT name FROM tools_category WHERE is_archived IS FALSE ORDER BY created_at ASC", String.class);
        String tail = "?page=1&size=20&sort=createdAt,asc";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        CategoryFilterResponse.class,
                        dto -> {
                            List<String> categoryNamesResponse = dto.categories().stream().map(Category::getName).toList();
                            assertIterableEquals(categoryNames, categoryNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link CategoryController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date desc,
     * should return sorted categories by create date desc.
     * Test returns category names from database(using jdbcTemplate) ordered by created date desc.
     * Then test make request to find all categories and checks if it returns the categories in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_create_date_desc_categories_test() throws Exception {
        List<String> categoryNames = jdbcTemplate.queryForList("SELECT name FROM tools_category WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);

        mockMvc.perform(get(urlEndpoint() + "?page=1&size=20&sort=createdAt,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        CategoryFilterResponse.class,
                        dto -> {
                            List<String> categoryNamesResponse = dto.categories().stream().map(Category::getName).toList();
                            assertIterableEquals(categoryNames, categoryNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link CategoryController#findAll(int, int, String, Boolean, String)}  without filter by default
     * should return sorted categories by create date desc.
     * Test returns category names from database(using jdbcTemplate) ordered by created date desc.
     * Then test make request to find all categories and checks if it returns the categories in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_create_date_desc_categories_by_default_test() throws Exception {
        List<String> categoryNames = jdbcTemplate.queryForList("SELECT name FROM tools_category WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);
        String tail = "?page=1&size=20";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        CategoryFilterResponse.class,
                        dto -> {
                            List<String> categoryNamesResponse = dto.categories().stream().map(Category::getName).toList();
                            assertIterableEquals(categoryNames, categoryNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link CategoryController#update(Long, CategoryRequest)}  should update {@link Category} name field.
     * Test finds existing category id in database with jdbcTemplate.
     * Then send request for update category name by id.
     * Then checks if name was updated or not (by compare {@link CategoryRequest} name and categoryName received from database).
     */
    @Test
    public void update_should_update_category_name_test() throws Exception {
        long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1' AND is_archived IS FALSE", Long.class);
        CategoryRequest rq = new CategoryRequest("HandTool", false);

        mockMvc.perform(put(urlEndpoint() + "/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isNoContent());

        String newCategoryName = jdbcTemplate.queryForObject("SELECT name FROM tools_category WHERE category_id = " + categoryId + " AND is_archived IS FALSE", String.class);
        assertEquals(rq.name(), newCategoryName);
    }

    /**
     * {@link CategoryController#update(Long, CategoryRequest)} should update {@link Category} isArchived flag.
     * Test finds existing category id in database with jdbcTemplate.
     * Then send request for update isArchived flag by id.
     * Then checks if isArchived was updated or not (by compare {@link CategoryRequest} isArchived flag and flag received from database).
     */
    @Test
    public void update_should_update_category_is_archived_test() throws Exception {
        long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1' AND is_archived IS FALSE", Long.class);
        CategoryRequest rq = new CategoryRequest("category_1", true);

        mockMvc.perform(put(urlEndpoint() + "/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isNoContent());

        Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM tools_category WHERE category_id = " + categoryId, Boolean.class);
        assertEquals(rq.isArchived(), isArchived);
    }

    /**
     * {@link CategoryController#update(Long, CategoryRequest)} should return bad request with null category name.
     * Test finds existing category id in database with jdbcTemplate.
     * Then send request for update by id with null category name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_null_name_test() throws Exception {
        long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1' AND is_archived IS FALSE", Long.class);
        CategoryRequest rq = new CategoryRequest(null, true);

        mockMvc.perform(put(urlEndpoint() + "/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link CategoryController#update(Long, CategoryRequest)} should return bad request with blank category name.
     * Test finds existing category id in database with jdbcTemplate.
     * Then send request for update by id with blank category name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_blank_name_test() throws Exception {
        long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1' AND is_archived IS FALSE", Long.class);
        CategoryRequest rq = new CategoryRequest(" ", true);

        mockMvc.perform(put(urlEndpoint() + "/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link CategoryController#update(Long, CategoryRequest)} should return bad request with empty category name.
     * Test finds existing category id in database with jdbcTemplate.
     * Then send request for update by id with empty category name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_empty_name_test() throws Exception {
        long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_1' AND is_archived IS FALSE", Long.class);
        CategoryRequest rq = new CategoryRequest("", true);

        mockMvc.perform(put(urlEndpoint() + "/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link CategoryController#update(Long, CategoryRequest)} should return bad request if category name already exists in database.
     * Test finds existing category name in database with jdbcTemplate.
     * Then finds another category by id with different in database with jdbcTemplate.
     * Then send request for update by id with existing category name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_existing_name_test() throws Exception {
        String existingCategoryName = jdbcTemplate.queryForObject("SELECT name FROM tools_category WHERE name = 'category_1' AND is_archived IS FALSE", String.class);
        long categoryId = jdbcTemplate.queryForObject("SELECT category_id FROM tools_category WHERE name = 'category_2' AND is_archived IS FALSE", Long.class);
        CategoryRequest rq = new CategoryRequest(existingCategoryName, false);

        mockMvc.perform(put(urlEndpoint() + "/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link CategoryController#update(Long, CategoryRequest)} should return bad request if category with searching id not exist in database.
     * Test send request for update by not existing id.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_not_existing_id_test() throws Exception {
        CategoryRequest rq = new CategoryRequest("HandTool", true);

        mockMvc.perform(put(urlEndpoint() + "/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link CategoryController#save(CategoryRequest)} should save {@link Category} object.
     * Test checks if categories with given name not exists in database.
     * Then sends request to create new category and checks status equals created.
     * Then receive number categories exists in database with new name.
     * Then cheks if number categories with new name equals one (new category saved to database).
     */
    @Test
    public void save_should_save_new_category_test() throws Exception {
        String categoryName = "HandTool";
        Long countCategories = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_category WHERE name = '" + categoryName + "' AND is_archived IS FALSE", Long.class);
        assertEquals(0, countCategories);

        CategoryRequest rq = new CategoryRequest(categoryName, false);

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isCreated());

        Long countSavedCategories = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_category WHERE name = '" + categoryName + "' AND is_archived IS FALSE", Long.class);
        assertEquals(1L, countSavedCategories);
    }

    /**
     * {@link CategoryController#save(CategoryRequest)} should not save {@link Category} object if brnad already exists in database.
     * Test sends request to create new category with already existing category name in database.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_category_exists_test() throws Exception {
        CategoryRequest rq = new CategoryRequest("category_1", false);

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link CategoryController#save(CategoryRequest)} should not save {@link Category} object if category name is null.
     * Test sends request to create new category with null category name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_name_null_test() throws Exception {
        CategoryRequest rq = new CategoryRequest(null, false);

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link CategoryController#save(CategoryRequest)} should not save {@link Category} object if category name is empty.
     * Test sends request to create new category with empty category name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_name_empty_test() throws Exception {
        CategoryRequest rq = new CategoryRequest("", false);

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link CategoryController#save(CategoryRequest)} should not save {@link Category} object if category name is blank.
     * Test sends request to create new category with blank category name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_name_blank_test() throws Exception {
        CategoryRequest rq = new CategoryRequest("  ", false);

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link CategoryController#save(CategoryRequest)} should not save {@link Category} object if archived flag is null.
     * Test sends request to create new category with null archived flag.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_is_archived_null_test() throws Exception {
        CategoryRequest rq = new CategoryRequest("HandTool", null);

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }
}
