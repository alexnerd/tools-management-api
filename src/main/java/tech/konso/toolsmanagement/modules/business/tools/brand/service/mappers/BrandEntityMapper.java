package tech.konso.toolsmanagement.modules.business.tools.brand.service.mappers;

import tech.konso.toolsmanagement.modules.business.tools.brand.controller.dto.BrandRequest;
import tech.konso.toolsmanagement.modules.business.tools.brand.persistence.dao.Brand;

/**
 * Mapper for brand entity
 */

public class BrandEntityMapper {

    /**
     * Converts {@link BrandRequest} to {@link Brand} object.
     * <p>
     * Example:
     * <pre>
     *     toEntity(new Brand(), rq);
     * </pre>
     *
     * @param brand {@link Brand} object for save to database or update existing
     * @param rq {@link BrandRequest} object for converting to {@link Brand}
     * @return {@link Brand} saved object
     */
    public Brand toEntity(Brand brand, BrandRequest rq) {
        brand.setName(rq.name());
        brand.setIsArchived(rq.isArchived());
        return brand;
    }
}

