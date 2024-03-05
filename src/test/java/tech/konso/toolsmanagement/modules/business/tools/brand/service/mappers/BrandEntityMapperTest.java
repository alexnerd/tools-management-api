package tech.konso.toolsmanagement.modules.business.tools.brand.service.mappers;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tech.konso.toolsmanagement.modules.business.tools.brand.controller.dto.BrandRequest;
import tech.konso.toolsmanagement.modules.business.tools.brand.persistence.dao.Brand;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for BrandEntityMapper. Test for mapping fields and null values.
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
public class BrandEntityMapperTest {

    private BrandEntityMapper mapper;

    @BeforeAll
    public void init() {
        mapper = new BrandEntityMapper();
    }

    /**
     * {@link BrandEntityMapper#toEntity(Brand, BrandRequest)} should map {@link Brand} name field.
     * Test creates object {@link BrandRequest} with non-null test field and try to map it to {@link Brand} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_brand_should_map_name() {
        String brandName = "name";
        BrandRequest rq = BrandRequest.builder()
                .name(brandName)
                .build();

        Brand brand = mapper.toEntity(new Brand(), rq);

        assertEquals(brandName, brand.getName());
    }

    /**
     * {@link BrandEntityMapper#toEntity(Brand, BrandRequest)} should map {@link Brand} isArchived field.
     * Test creates object {@link BrandRequest} with non-null test field and try to map it to {@link Brand} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_brand_should_map_is_archived() {
        boolean isArchived = true;
        BrandRequest rq = BrandRequest.builder()
                .isArchived(isArchived)
                .build();

        Brand brand = mapper.toEntity(new Brand(), rq);

        assertEquals(isArchived, brand.getIsArchived());
    }
}
