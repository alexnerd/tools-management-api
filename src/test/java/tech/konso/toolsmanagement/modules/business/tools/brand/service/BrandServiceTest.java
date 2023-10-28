package tech.konso.toolsmanagement.modules.business.tools.brand.service;

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
import tech.konso.toolsmanagement.modules.business.tools.brand.controller.dto.BrandFilterResponse;
import tech.konso.toolsmanagement.modules.business.tools.brand.controller.dto.BrandRequest;
import tech.konso.toolsmanagement.modules.business.tools.brand.persistence.dao.Brand;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tech.konso.toolsmanagement.modules.business.tools.brand.persistence.specification.BrandSpecification.*;
import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

/**
 * Brand service layer tests.
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
public class BrandServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BrandService service;

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

    private BrandRequest.BrandRequestBuilder getDefaultBrandRequest() {
        return BrandRequest.builder()
                .name("MAKITA")
                .isArchived(false);
    }

    /**
     * {@link BrandService#findById(Long)} should return {@link Brand} by id from database.
     * Test checks equality brandId (received from jdbcTemplate request)
     * with id of brand object received from {@link BrandService#findById(Long)}
     */
    @Test
    public void findById_should_return_brand_test() {
        long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1' AND is_archived IS FALSE", Long.class);

        Brand brand = service.findById(brandId);

        assertEquals(brandId, brand.getId());
    }

    /**
     * {@link BrandService#findById(Long)} should throw {@link BPException} exception
     * if {@link Brand} with id not exist in database.
     * Test try to find brand whit id = -1 (negative num,ber guaranties, that no such id exists in database)
     * and check if {@link BPException} is thrown.
     */
    @Test
    public void findById_should_throw_exception_on_not_fount_brand_test() {
        long brandId = -1;

        assertThrows(BPException.class, () -> service.findById(brandId));
    }

    /**
     * {@link BrandService#save(BrandRequest)} should update {@link Brand} name field.
     * Test finds existing brand id in database with jdbcTemplate and try to update it name
     * using {@link BrandService#save(BrandRequest)}.
     * Then checks if name was updated or not (by compare {@link BrandRequest} name and brandName received from database).
     */
    @Test
    public void save_should_update_brand_name_test() {
        long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1' AND is_archived IS FALSE", Long.class);
        BrandRequest rq = getDefaultBrandRequest()
                .id(brandId)
                .name("new_brand")
                .build();

        service.save(rq);

        String brandName = jdbcTemplate.queryForObject("SELECT name FROM tools_brand WHERE brand_id = " + brandId + " AND is_archived IS FALSE", String.class);
        assertEquals(rq.name(), brandName);
    }

    /**
     * {@link BrandService#save(BrandRequest)} should update {@link Brand} isArchived field.
     * Test finds existing brand id in database with jdbcTemplate and try to update it isArchived flag
     * using {@link BrandService#save(BrandRequest)}.
     * Then checks if isArchived flag was updated or not (using assertTrue on field).
     */
    @Test
    public void save_should_archive_brand_test() {
        long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1' AND is_archived IS FALSE", Long.class);
        BrandRequest rq = getDefaultBrandRequest()
                .id(brandId)
                .isArchived(true)
                .build();

        service.save(rq);

        Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM tools_brand WHERE brand_id = " + brandId, Boolean.class);
        assertTrue(isArchived);
    }

    /**
     * {@link BrandService#save(BrandRequest)} should not update {@link Brand} if name field is null.
     * Test finds existing brand id in database with jdbcTemplate and try to update it name field
     * using {@link BrandService#save(BrandRequest)}.
     * Then checks if exception {@link DataIntegrityViolationException} was thrown.
     * Then checks if field name not changed during test.
     */
    @Test
    public void save_should_not_update_null_name_test() {
        String brandName = "brand_1";
        long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = '" + brandName + "' AND is_archived IS FALSE", Long.class);
        BrandRequest rq = getDefaultBrandRequest()
                .id(brandId)
                .name(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        String brandNameFromDb = jdbcTemplate.queryForObject("SELECT name FROM tools_brand WHERE brand_id = " + brandId + " AND is_archived IS FALSE", String.class);
        assertEquals(brandName, brandNameFromDb);
    }

    /**
     * {@link BrandService#save(BrandRequest)} should not update {@link Brand} if isArchived flag is null.
     * Test finds existing brand id in database with jdbcTemplate and try to update it isArchived flag
     * using {@link BrandService#save(BrandRequest)}.
     * Then checks if exception {@link DataIntegrityViolationException} was thrown.
     * Then checks if isArchived flag not changed during test.
     */
    @Test
    public void save_should_not_update_null_isArchived_test() {
        long brandId = jdbcTemplate.queryForObject("SELECT brand_id FROM tools_brand WHERE name = 'brand_1' AND is_archived IS FALSE", Long.class);
        BrandRequest rq = getDefaultBrandRequest()
                .id(brandId)
                .isArchived(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM tools_brand WHERE brand_id = " + brandId, Boolean.class);
        assertFalse(isArchived);
    }

    /**
     * {@link BrandService#save(BrandRequest)}} should save {@link Brand} object.
     * Test creates dto object {@link BrandRequest} and then using {@link BrandService#save(BrandRequest)}
     * try to save new {@link Brand} object to database.
     * Then checks returns {@link Brand} object if id not null, name field and isArchived flag equals this fields
     * from dto object {@link BrandRequest}.
     */
    @Test
    public void save_should_save_brand_test() {
        BrandRequest rq = getDefaultBrandRequest()
                .name("new_brand")
                .build();

        Brand savedBrand = service.save(rq);

        assertNotNull(savedBrand.getId());
        assertEquals(rq.name(), savedBrand.getName());
        assertEquals(rq.isArchived(), savedBrand.getIsArchived());
    }

    /**
     * {@link BrandService#save(BrandRequest)}} should not save {@link Brand} object
     * if field name already exists in database.
     * Test try to save Brand with existing field name and check if {@link DataIntegrityViolationException} is thrown.
     * Then check if only one brand with given name exist in database.
     */
    @Test
    public void save_should_not_save_if_brand_name_already_exists_test() {
        String brandName = "brand_1";
        BrandRequest rq = getDefaultBrandRequest()
                .name(brandName)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_brand WHERE name = '" + brandName + "'", Long.class);
        assertEquals(1L, count);
    }

    /**
     * {@link BrandService#save(BrandRequest)}} should not save {@link Brand} object if field name is null.
     * Test try to save Brand with null field name and check if {@link DataIntegrityViolationException} is thrown.
     * Then test checks if there is no brands with null name exists in the database
     */
    @Test
    public void save_should_not_save_if_brand_name_is_null_exists_test() {
        BrandRequest rq = getDefaultBrandRequest()
                .name(null)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> service.save(rq));

        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_brand WHERE name IS NULL", Long.class);
        assertEquals(0L, count);
    }

    /**
     * {@link BrandService#findAll(int, int, Specification)} should return all not archived {@link Brand} objects.
     * Test counts all not archived brand objects in the database using jdbcTemplate
     * Then test build isArchived specification for not archived brands and try to find them with {@link BrandService#findAll(int, int, Specification)}
     * Then test checks if counts not archived brands from jdbcTemplate equals totalItems from {@link BrandFilterResponse}
     * received from {@link BrandService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_all_not_archived_brands_test() {
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_brand WHERE is_archived IS FALSE", Long.class);
        Specification<Brand> spec = specBuilder(isArchivedSpec(false)).build();

        Page<Brand> foundedBrands = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedBrands.getTotalElements());
    }

    /**
     * {@link BrandService#findAll(int, int, Specification)} should return all archived {@link Brand} objects.
     * Test counts all archived brand objects in the database using jdbcTemplate
     * Then test build isArchived specification for archived brands and try to find them with {@link BrandService#findAll(int, int, Specification)}
     * Then test checks if counts archived brands from jdbcTemplate equals totalItems from {@link BrandFilterResponse}
     * received from {@link BrandService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_all_archived_brands_test() {
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_brand WHERE is_archived IS TRUE", Long.class);
        Specification<Brand> spec = specBuilder(isArchivedSpec(true)).build();

        Page<Brand> foundedBrands = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedBrands.getTotalElements());
    }

    /**
     * {@link BrandService#findAll(int, int, Specification)} should return {@link Brand} objects by like name pattern.
     * Test counts all brand objects in the database matches %ran% pattern using jdbcTemplate
     * Then test build like specification for name with %ran% pattern and try to find them with {@link BrandService#findAll(int, int, Specification)}
     * Then test checks if counts brands from jdbcTemplate equals totalItems from {@link BrandFilterResponse}
     * received from {@link BrandService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_find_by_name_brands_test() {
        String likeName = "ran";
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('Makita')");
        jdbcTemplate.update("INSERT INTO tools_brand (name) VALUES ('DDBRANDDD')");
        long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM tools_brand WHERE LOWER (name) LIKE '%" + likeName + "%'", Long.class);
        Specification<Brand> spec = specBuilder(likeSpec(likeName)).build();

        Page<Brand> foundedBrands = service.findAll(0, 100, spec);

        assertEquals(countNotArchived, foundedBrands.getTotalElements());
    }

    /**
     * {@link BrandService#findAll(int, int, Specification)} should sort {@link Brand} objects by name in asc order.
     * Test receives all brand names from the database in asc order
     * Then test build sort specification for order brands by name in asc order and try to find them with {@link BrandService#findAll(int, int, Specification)}
     * Then test checks if order of brand names received from jdbcTemplate equals order of brand names from {@link BrandFilterResponse}
     * received from {@link BrandService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_asc_by_name_brands_test() {
        List<String> brandNames = jdbcTemplate.queryForList("SELECT name FROM tools_brand ORDER BY name ASC", String.class);
        Specification<Brand> spec = specBuilder(sortSpec("name,asc")).build();

        Page<Brand> foundedBrands = service.findAll(0, 100, spec);

        List<String> resultBrandNames = foundedBrands.getContent().stream().map(Brand::getName).toList();
        assertIterableEquals(brandNames, resultBrandNames);
    }

    /**
     * {@link BrandService#findAll(int, int, Specification)} should sort {@link Brand} objects by name in desc order.
     * Test receives all brand names from the database in desc order
     * Then test build sort specification for order brands by name in desc order and try to find them with {@link BrandService#findAll(int, int, Specification)}
     * Then test checks if order of brand names received from jdbcTemplate equals order of brand names from {@link BrandFilterResponse}
     * received from {@link BrandService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_desc_by_name_brands_test() {
        List<String> brandNames = jdbcTemplate.queryForList("SELECT name FROM tools_brand ORDER BY name DESC", String.class);
        Specification<Brand> spec = specBuilder(sortSpec("name,desc")).build();

        Page<Brand> foundedBrands = service.findAll(0, 100, spec);

        List<String> resultBrandNames = foundedBrands.getContent().stream().map(Brand::getName).toList();
        assertIterableEquals(brandNames, resultBrandNames);
    }

    /**
     * {@link BrandService#findAll(int, int, Specification)} should sort {@link Brand} objects by createdAt in asc order.
     * Test receives all brand names from the database ordered by createdAt in asc order
     * Then test build sort specification for order brands by createdAt in asc order and try to find them with {@link BrandService#findAll(int, int, Specification)}
     * Then test checks if order of brand names received from jdbcTemplate equals order of brand names from {@link BrandFilterResponse}
     * received from {@link BrandService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_asc_by_created_date_brands_test() {
        List<String> brandNames = jdbcTemplate.queryForList("SELECT name FROM tools_brand ORDER BY created_at ASC", String.class);
        Specification<Brand> spec = specBuilder(sortSpec("createdat,asc")).build();

        Page<Brand> foundedBrands = service.findAll(0, 100, spec);

        List<String> resultBrandNames = foundedBrands.getContent().stream().map(Brand::getName).toList();
        assertIterableEquals(brandNames, resultBrandNames);
    }

    /**
     * {@link BrandService#findAll(int, int, Specification)} should sort {@link Brand} objects by createdAt in desc order.
     * Test receives all brand names from the database ordered by createdAt in desc order
     * Then test build sort specification for order brands by createdAt in desc order and try to find them with {@link BrandService#findAll(int, int, Specification)}
     * Then test checks if order of brand names received from jdbcTemplate equals order of brand names from {@link BrandFilterResponse}
     * received from {@link BrandService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_should_sort_desc_by_created_date_brands_test() {
        List<String> brandNames = jdbcTemplate.queryForList("SELECT name FROM tools_brand ORDER BY created_at DESC", String.class);
        Specification<Brand> spec = specBuilder(sortSpec("createdat,desc")).build();

        Page<Brand> foundedBrands = service.findAll(0, 100, spec);

        List<String> resultBrandNames = foundedBrands.getContent().stream().map(Brand::getName).toList();
        assertIterableEquals(brandNames, resultBrandNames);
    }

    /**
     * {@link BrandService#findAll(int, int, Specification)} without filters, by default should sort {@link Brand} objects by createdAt in desc order.
     * Test receives all brand names from the database ordered by createdAt in desc order
     * Then test build sort specification null parameter and try to find brands with {@link BrandService#findAll(int, int, Specification)}
     * Then test checks if order of brand names received from jdbcTemplate equals order of brand names from {@link BrandFilterResponse}
     * received from {@link BrandService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_null_filter_should_sort_by_default_desc_by_created_date_brands_test() {
        List<String> brandNames = jdbcTemplate.queryForList("SELECT name FROM tools_brand ORDER BY created_at DESC", String.class);
        Specification<Brand> spec = specBuilder(sortSpec(null)).build();

        Page<Brand> foundedBrands = service.findAll(0, 100, spec);

        List<String> resultBrandNames = foundedBrands.getContent().stream().map(Brand::getName).toList();
        assertIterableEquals(brandNames, resultBrandNames);
    }

    /**
     * {@link BrandService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Brand} objects by createdAt in desc order.
     * Test receives all brand names from the database ordered by createdAt in desc order
     * Then test build sort specification with empty field "  " parameter and try to find brands with {@link BrandService#findAll(int, int, Specification)}
     * Then test checks if order of brand names received from jdbcTemplate equals order of brand names from {@link BrandFilterResponse}
     * received from {@link BrandService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_blank_filter_should_sort_by_default_desc_by_created_date_brands_test() {
        List<String> brandNames = jdbcTemplate.queryForList("SELECT name FROM tools_brand ORDER BY created_at DESC", String.class);
        Specification<Brand> spec = specBuilder(sortSpec(" ")).build();

        Page<Brand> foundedBrands = service.findAll(0, 100, spec);

        List<String> resultBrandNames = foundedBrands.getContent().stream().map(Brand::getName).toList();
        assertIterableEquals(brandNames, resultBrandNames);
    }

    /**
     * {@link BrandService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Brand} objects by createdAt in desc order.
     * Test receives all brand names from the database ordered by createdAt in desc order
     * Then test build sort specification with unsupported parameter and try to find brands with {@link BrandService#findAll(int, int, Specification)}
     * Then test checks if order of brand names received from jdbcTemplate equals order of brand names from {@link BrandFilterResponse}
     * received from {@link BrandService#findAll(int, int, Specification)}
     */
    @Test
    public void findAll_with_unsupported_filter_should_sort_by_default_desc_by_created_date_brands_test() {
        List<String> brandNames = jdbcTemplate.queryForList("SELECT name FROM tools_brand ORDER BY created_at DESC", String.class);
        Specification<Brand> spec = specBuilder(sortSpec("unsupported_filter")).build();

        Page<Brand> foundedBrands = service.findAll(0, 100, spec);

        List<String> resultBrandNames = foundedBrands.getContent().stream().map(Brand::getName).toList();
        assertIterableEquals(brandNames, resultBrandNames);
    }

    /**
     * {@link BrandService#findAll(int, int, Specification)} should return brands with specified size.
     * Test creates null specification.
     * Then test with page size 1 returns {@link BrandFilterResponse} from  {@link BrandService#findAll(int, int, Specification)}
     * Then test checks total received items and concrete size of founded brands in concrete page.
     */
    @Test
    public void findAll_with_page_size_one_should_return_one_brand_test() {
        Specification<Brand> spec = specBuilder(sortSpec(null)).build();

        Page<Brand> foundedBrands = service.findAll(0, 1, spec);

        assertEquals(6, foundedBrands.getTotalElements());
        assertEquals(1, foundedBrands.getContent().size());
    }
}