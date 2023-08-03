package tech.konso.toolsmanagement.modules.tools.business.brand.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.tools.business.brand.controller.dto.BrandRequest;
import tech.konso.toolsmanagement.modules.tools.business.brand.persistence.dao.Brand;
import tech.konso.toolsmanagement.modules.tools.business.brand.persistence.repository.BrandRepository;
import tech.konso.toolsmanagement.modules.tools.business.brand.persistence.specification.BrandSpecification;
import tech.konso.toolsmanagement.modules.tools.commons.AbstractSpecification;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;

import java.util.Optional;

import static tech.konso.toolsmanagement.modules.tools.commons.AbstractSpecification.specBuilder;

/**
 * Service layer for working with brands.
 */
@Service
public class BrandService {

    @Autowired
    private BrandRepository repository;

    /**
     * Find brand in database by unique id. Brand must exist in database
     * <p>
     * Example:
     * <pre>
     *     Brand brand = findById(2L);
     * </pre>
     *
     * @param id of brand, must exist in database
     * @return brand from database
     * @throws BPException if brand not exists in database
     */
    public Brand findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BPException("Brand not found id: " + id));
    }

    /**
     * Get brand reference by unique id. Used to link the brand entity with other entities,
     * when the entire object from the database should not be loaded
     * <p>
     * Example:
     * <pre>
     *     Brand brand = getReferenceById(2L);
     * </pre>
     *
     * @param id of brand, must exist in database
     * @return proxy brand object
     */
    public Brand getReference(Long id) {
        return repository.getReferenceById(id);
    }

    /**
     * Find brands by brand specification and returns it in pageable format.
     * By default, result set sorts by create date from newer to older and without archived brands.
     * <p>
     * Example:
     * <pre>
     *     Specification&lt;Brand> spec = specBuilder(sortSpec("name,desc")).build();
     *     Page<Brand> foundedBrands = service.findAll(0, 100, spec);
     * </pre>
     *
     * @param page number of returned result set
     * @param size of the returned page
     * @param spec set of brand specification
     * @return {@link Page<Brand>} object for resulting dataset in pageable format
     * @see BrandSpecification brand specifications
     */
    public Page<Brand> findAll(int page, int size, Specification<Brand> spec) {
        AbstractSpecification.SpecBuilder<Brand> builder = specBuilder(Brand.class);
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(builder.and(spec).build(), pageable);
    }

    /**
     * Save new brand to database or update existing.
     * Brand name must be unique and not exists in database.
     * <p>
     * Example:
     * <pre>
     *     BrandRequest rq = new BrandRequest(null, "new_brand", false);
     *     Brand savedBrand = service.save(rq);
     * </pre>
     *
     * @param rq {@link BrandRequest} object for creating brand
     * @return {@link Brand} saved object
     */
    @Transactional
    public Brand save(BrandRequest rq) {
        return Optional.ofNullable(rq.id())
                .map(id -> repository.findById(rq.id())
                        .orElseThrow(() -> new BPException("Brand not found id: " + id))
                ).map(brand -> toEntity(brand, rq))
                .orElseGet(() ->
                        repository.save(toEntity(new Brand(), rq))
                );
    }

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
    private Brand toEntity(Brand brand, BrandRequest rq) {
        brand.setName(rq.name());
        brand.setIsArchived(rq.isArchived());
        return brand;
    }
}