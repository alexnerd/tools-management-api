package tech.konso.toolsmanagement.modules.tools.business.brand.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import tech.konso.toolsmanagement.modules.tools.business.brand.controller.dto.BrandFilterResponse;
import tech.konso.toolsmanagement.modules.tools.business.brand.controller.dto.BrandRequest;
import tech.konso.toolsmanagement.modules.tools.business.brand.persistence.dao.Brand;
import tech.konso.toolsmanagement.modules.tools.business.brand.service.BrandService;
import tech.konso.toolsmanagement.modules.tools.commons.AbstractControllerTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Brand controller layer tests.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */
public class BrandControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;


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

    private String urlEndpoint() {
        return url + "/v1/tools/brands";
    }

    private BrandRequest.BrandRequestBuilder getDefaultBrandRequest() {
        return BrandRequest.builder()
                .name("MAKITA")
                .isArchived(false);
    }

    /**
     * {@link BrandController#find(Long)} should return {@link Brand} by id from database.
     * Test checks status code 200 and equality brandId (received from jdbcTemplate request)
     * with id of brand object received from {@link BrandService#findById(Long)} and name.
     */
    @Test
    public void find_should_return_brand_test() throws Exception {
        long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1' AND is_archived IS FALSE", Long.class);

        mockMvc.perform(get(urlEndpoint() + "/" + brandId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        Brand.class,
                        dto -> dto.getId() == brandId && dto.getName().equals("brand_1")
                )));
    }

    /**
     * {@link BrandController#find(Long)} should return bad request if {@link Brand} with id not exist in database.
     * Test try to find brand whit id = -1 (negative number guaranties, that no such id exists in database)
     * and check if controller return bad request with detailed error message in header.
     */
    @Test
    public void find_should_return_bad_request_test() throws Exception {
        long brandId = -1L;

        mockMvc.perform(get(urlEndpoint() + "/" + brandId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().stringValues("detail", "Brand not found id: " + brandId));
    }

    /**
     * {@link BrandController#findAll(int, int, String, Boolean, String)}
     * should return bad request with incorrect page number. Test try to search all brands whit page = 0
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
     * {@link BrandController#findAll(int, int, String, Boolean, String)}
     * should return bad request with incorrect size number. Test try to search all brands whit size = 99999
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
     * {@link BrandController#findAll(int, int, String, Boolean, String)}  without filters should return all not archived brands.
     * Test counts all not archived brands from the database.
     * Then test make request to find all brands and checks if it returns the same number, as plane jdbc request.
     */
    @Test
    public void findAll_should_return_brands_without_filters_test() throws Exception {
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_brand WHERE is_archived IS FALSE", Long.class);
        String tail = "?page=1&size=20";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        BrandFilterResponse.class,
                        dto -> dto.totalItems() == count)
                ));
    }

    /**
     * {@link BrandController#findAll(int, int, String, Boolean, String)}  with archived filter,
     * should return all archived brands.
     * Test counts all archived brands from the database.
     * Then test make request to find all brands and checks if it returns the same number, as plane jdbc request.
     */
    @Test
    public void findAll_should_filter_archived_brands_test() throws Exception {
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_brand WHERE is_archived IS TRUE", Long.class);
        String tail = "?page=1&size=20&isArchived=true";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        BrandFilterResponse.class,
                        dto -> dto.totalItems() == count)
                ));
    }

    /**
     * {@link BrandController#findAll(int, int, String, Boolean, String)}  with not archived filter,
     * should return all not archived brands.
     * Test counts all not archived brands from the database.
     * Then test make request to find all brands and checks if it returns the same number, as plane jdbc request.
     */
    @Test
    public void findAll_should_filter_not_archived_brands_test() throws Exception {
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_brand WHERE is_archived IS FALSE", Long.class);
        String tail = "?page=1&size=20&isArchived=false";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        BrandFilterResponse.class,
                        dto -> dto.totalItems() == count)
                ));
    }

    /**
     * {@link BrandController#findAll(int, int, String, Boolean, String)}  with filter by full name,
     * should return brand with this name.
     * Test counts all brand with concrete name.
     * Then test make request to find all brands and checks if it returns the same number, as plane jdbc request,
     * and checks if returns name equals with predefined name.
     */
    @Test
    public void findAll_should_filter_full_name_brands_test() throws Exception {
        String brandName = "brand_1";
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_brand WHERE name = '" + brandName + "' AND is_archived IS FALSE", Long.class);
        String tail = "?page=1&size=20&name=" + brandName;

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        BrandFilterResponse.class,
                        dto -> dto.totalItems() == count && dto.brands().get(0).getName().equals(brandName))
                ));
    }

    /**
     * {@link BrandController#findAll(int, int, String, Boolean, String)}  with filter by part of name,
     * should return brand with this name.
     * Test counts all brand with like filter by name.
     * Then test make request to find all brands and checks if it returns the same number, as plane jdbc request.
     */
    @Test
    public void findAll_should_filter_like_name_brands_test() throws Exception {
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('Makita')");
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('DDBRANDDD')");
        String brandName = "rand";
        long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_brand WHERE LOWER (name) LIKE '%" + brandName + "%' AND is_archived IS FALSE", Long.class);
        String tail = "?page=1&size=20&name=" + brandName;

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        BrandFilterResponse.class,
                        dto -> dto.totalItems() == count)
                ));
    }

    /**
     * {@link BrandController#findAll(int, int, String, Boolean, String)}  with filter sorting by name asc,
     * should return sorted brands by names asc.
     * Test returns brand names from database(using jdbcTemplate) ordered by name asc.
     * Then test make request to find all brands and checks if it returns the brands in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_name_asc_brands_test() throws Exception {
        List<String> brandNames = jdbcTemplate.queryForList("SELECT name FROM tools_brand WHERE is_archived IS FALSE ORDER BY name ASC", String.class);
        String tail = "?page=1&size=20&sort=name,asc";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        BrandFilterResponse.class,
                        dto -> {
                            List<String> brandNamesResponse = dto.brands().stream().map(Brand::getName).toList();
                            assertIterableEquals(brandNames, brandNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link BrandController#findAll(int, int, String, Boolean, String)}  with filter sorting by name desc,
     * should return sorted brands by name desc.
     * Test returns brand names from database(using jdbcTemplate) ordered by name desc.
     * Then test make request to find all brands and checks if it returns the brands in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_name_desc_brands_test() throws Exception {
        List<String> brandNames = jdbcTemplate.queryForList("SELECT name FROM tools_brand WHERE is_archived IS FALSE ORDER BY name DESC", String.class);
        String tail = "?page=1&size=20&sort=name,desc";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        BrandFilterResponse.class,
                        dto -> {
                            List<String> brandNamesResponse = dto.brands().stream().map(Brand::getName).toList();
                            assertIterableEquals(brandNames, brandNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link BrandController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date asc,
     * should return sorted brands by create date asc.
     * Test returns brand names from database(using jdbcTemplate) ordered by created date asc.
     * Then test make request to find all brands and checks if it returns the brands in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_create_date_asc_brands_test() throws Exception {
        List<String> brandNames = jdbcTemplate.queryForList("SELECT name FROM tools_brand WHERE is_archived IS FALSE ORDER BY created_at ASC", String.class);
        String tail = "?page=1&size=20&sort=createdAt,asc";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        BrandFilterResponse.class,
                        dto -> {
                            List<String> brandNamesResponse = dto.brands().stream().map(Brand::getName).toList();
                            assertIterableEquals(brandNames, brandNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link BrandController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date desc,
     * should return sorted brands by create date desc.
     * Test returns brand names from database(using jdbcTemplate) ordered by created date desc.
     * Then test make request to find all brands and checks if it returns the brands in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_create_date_desc_brands_test() throws Exception {
        List<String> brandNames = jdbcTemplate.queryForList("SELECT name FROM tools_brand WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);

        mockMvc.perform(get(urlEndpoint() + "?page=1&size=20&sort=createdAt,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        BrandFilterResponse.class,
                        dto -> {
                            List<String> brandNamesResponse = dto.brands().stream().map(Brand::getName).toList();
                            assertIterableEquals(brandNames, brandNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link BrandController#findAll(int, int, String, Boolean, String)}  without filter by default
     * should return sorted brands by create date desc.
     * Test returns brand names from database(using jdbcTemplate) ordered by created date desc.
     * Then test make request to find all brands and checks if it returns the brands in the same order, as plane jdbc request.
     */
    @Test
    public void findAll_should_sort_by_create_date_desc_brands_by_default_test() throws Exception {
        List<String> brandNames = jdbcTemplate.queryForList("SELECT name FROM tools_brand WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);
        String tail = "?page=1&size=20";

        mockMvc.perform(get(urlEndpoint() + tail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dtoMatcher(
                        BrandFilterResponse.class,
                        dto -> {
                            List<String> brandNamesResponse = dto.brands().stream().map(Brand::getName).toList();
                            assertIterableEquals(brandNames, brandNamesResponse);
                            return true;
                        })
                ));
    }

    /**
     * {@link BrandController#update(BrandRequest)}  should update {@link Brand} name field.
     * Test finds existing brand id in database with jdbcTemplate.
     * Then send request for update brand name by id.
     * Then checks if name was updated or not (by compare {@link BrandRequest} name and brandName received from database).
     */
    @Test
    public void update_should_update_brand_name_test() throws Exception {
        long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1' AND is_archived IS FALSE", Long.class);
        BrandRequest rq = getDefaultBrandRequest()
                .id(brandId)
                .build();

        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isNoContent());

        String newBrandName = jdbcTemplate.queryForObject("SELECT name FROM tools_brand WHERE brand_id = " + brandId + " AND is_archived IS FALSE", String.class);
        assertEquals(rq.name(), newBrandName);
    }

    /**
     * {@link BrandController#update(BrandRequest)} should update {@link Brand} isArchived flag.
     * Test finds existing brand id in database with jdbcTemplate.
     * Then send request for update isArchived flag by id.
     * Then checks if isArchived was updated or not (by compare {@link BrandRequest} isArchived flag and flag received from database).
     */
    @Test
    public void update_should_update_brand_is_archived_test() throws Exception {
        long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1' AND is_archived IS FALSE", Long.class);
        BrandRequest rq = getDefaultBrandRequest()
                .id(brandId)
                .isArchived(true)
                .build();

        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isNoContent());

        Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM tools_brand WHERE brand_id = " + brandId, Boolean.class);
        assertEquals(rq.isArchived(), isArchived);
    }

    /**
     * {@link BrandController#update(BrandRequest)} should return bad request with null brand name.
     * Test finds existing brand id in database with jdbcTemplate.
     * Then send request for update by id with null brand name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_null_name_test() throws Exception {
        long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1' AND is_archived IS FALSE", Long.class);
        BrandRequest rq = getDefaultBrandRequest()
                .id(brandId)
                .name(null)
                .build();


        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link BrandController#update(BrandRequest)} should return bad request with blank brand name.
     * Test finds existing brand id in database with jdbcTemplate.
     * Then send request for update by id with blank brand name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_blank_name_test() throws Exception {
        long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1' AND is_archived IS FALSE", Long.class);
        BrandRequest rq = getDefaultBrandRequest()
                .id(brandId)
                .name("  ")
                .build();


        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link BrandController#update(BrandRequest)} should return bad request with empty brand name.
     * Test finds existing brand id in database with jdbcTemplate.
     * Then send request for update by id with empty brand name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_empty_name_test() throws Exception {
        long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1' AND is_archived IS FALSE", Long.class);
        BrandRequest rq = getDefaultBrandRequest()
                .id(brandId)
                .name("")
                .build();


        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link BrandController#update(BrandRequest)} should return bad request if brand name already exists in database.
     * Test finds existing brand name in database with jdbcTemplate.
     * Then finds another brand by id with different in database with jdbcTemplate.
     * Then send request for update by id with existing brand name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_existing_name_test() throws Exception {
        String existingBrandName = jdbcTemplate.queryForObject("SELECT name FROM tools_brand WHERE name = 'brand_1' AND is_archived IS FALSE", String.class);
        long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_2' AND is_archived IS FALSE", Long.class);
        BrandRequest rq = getDefaultBrandRequest()
                .id(brandId)
                .name(existingBrandName)
                .build();

        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link BrandController#update(BrandRequest)} should return bad request if brand with searching id not exist in database.
     * Test send request for update by not existing id.
     * Then checks if controller response with bad request.
     */
    @Test
    public void update_should_return_bad_request_for_not_existing_id_test() throws Exception {
        BrandRequest rq = getDefaultBrandRequest()
                .id(-1L)
                .name("MAKITA")
                .build();

        mockMvc.perform(put(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link BrandController#save(BrandRequest)} should save {@link Brand} object.
     * Test checks if brands with given name not exists in database.
     * Then sends request to create new brand and checks status equals created.
     * Then receive number brands exists in database with new name.
     * Then cheks if number brands with new name equals one (new brand saved to database).
     */
    @Test
    public void save_should_save_new_brand_test() throws Exception {
        BrandRequest rq = getDefaultBrandRequest()
                .build();
        Long countBrands = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_brand WHERE name = '" + rq.name() + "' AND is_archived IS FALSE", Long.class);
        assertEquals(0, countBrands);


        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isCreated());

        Long countSavedBrands = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_brand WHERE name = '" + rq.name() + "' AND is_archived IS FALSE", Long.class);
        assertEquals(1L, countSavedBrands);
    }

    /**
     * {@link BrandController#save(BrandRequest)} should not save {@link Brand} object if brnad already exists in database.
     * Test sends request to create new brand with already existing brand name in database.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_brand_exists_test() throws Exception {
        BrandRequest rq = getDefaultBrandRequest()
                .name("brand_1")
                .build();

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link BrandController#save(BrandRequest)} should not save {@link Brand} object if brand name is null.
     * Test sends request to create new brand with null brand name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_name_null_test() throws Exception {
        BrandRequest rq = getDefaultBrandRequest()
                .name(null)
                .build();

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link BrandController#save(BrandRequest)} should not save {@link Brand} object if brand name is empty.
     * Test sends request to create new brand with empty brand name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_name_empty_test() throws Exception {
        BrandRequest rq = getDefaultBrandRequest()
                .name("")
                .build();

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link BrandController#save(BrandRequest)} should not save {@link Brand} object if brand name is blank.
     * Test sends request to create new brand with blank brand name.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_name_blank_test() throws Exception {
        BrandRequest rq = getDefaultBrandRequest()
                .name("  ")
                .build();

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * {@link BrandController#save(BrandRequest)} should not save {@link Brand} object if archived flag is null.
     * Test sends request to create new brand with null archived flag.
     * Then checks if controller response with bad request.
     */
    @Test
    public void save_should_not_save_if_is_archived_null_test() throws Exception {
        BrandRequest rq = getDefaultBrandRequest()
                .isArchived(null)
                .build();

        mockMvc.perform(post(urlEndpoint())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isBadRequest());
    }
}
