package tech.konso.toolsmanagement.modules.tools.business.label.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.tools.business.label.controller.dto.LabelRequest;
import tech.konso.toolsmanagement.modules.tools.business.label.persistence.dao.Label;
import tech.konso.toolsmanagement.modules.tools.business.label.persistence.repository.LabelRepository;
import tech.konso.toolsmanagement.modules.tools.business.label.persistence.specification.LabelSpecification;
import tech.konso.toolsmanagement.modules.tools.commons.AbstractSpecification;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;

import java.util.Optional;

import static tech.konso.toolsmanagement.modules.tools.commons.AbstractSpecification.specBuilder;

/**
 * Service layer for working with labels.
 */
@Service("ToolsLabelService")
public class LabelService {

    @Autowired
    private LabelRepository repository;

    /**
     * Find label in database by unique id. Label must exist in database
     * <p>
     * Example:
     * <pre>
     *     Label label = findById(2L);
     * </pre>
     *
     * @param id of label, must exist in database
     * @return label from database
     * @throws BPException if label not exists in database
     */
    public Label findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BPException("Label not found id: " + id));
    }

    /**
     * Get label reference by unique id. Used to link the label entity with other entities,
     * when the entire object from the database should not be loaded
     * <p>
     * Example:
     * <pre>
     *     Label label = getReferenceById(2L);
     * </pre>
     *
     * @param id of label, must exist in database
     * @return proxy label object
     */
    public Label getReference(Long id) {
        return repository.getReferenceById(id);
    }

    /**
     * Finds labels by label specification and returns it in pageable format.
     * By default, result set sorts by create date from newer to older and without archived labels.
     * <p>
     * Example:
     * <pre>
     *     Specification&lt;Label> spec = specBuilder(sortSpec("name,desc")).build();
     *     Page<Label> foundedLabels = service.findAll(0, 100, spec);
     * </pre>
     *
     * @param page number of returned result set
     * @param size of the returned page
     * @param spec set of label specification
     * @return {@link Page<Label>} object for resulting dataset in pageable format
     * @see LabelSpecification label specifications
     */
    public Page<Label> findAll(int page, int size, Specification<Label> spec) {
        AbstractSpecification.SpecBuilder<Label> builder = specBuilder(Label.class);
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(builder.and(spec).build(), pageable);
    }

    /**
     * Save new label to database or update existing.
     * Label name must be unique and not exists in database.
     * <p>
     * Example:
     * <pre>
     *     LabelRequest rq = new LabelRequest(null, "new_label", false);
     *     Label savedLabel = service.save(rq);
     * </pre>
     *
     * @param rq {@link LabelRequest} object for creating label
     * @return {@link Label} saved object
     */
    @Transactional
    public Label save(LabelRequest rq) {
        return Optional.ofNullable(rq.id())
                .map(id -> repository.findById(rq.id())
                        .orElseThrow(() -> new BPException("Label not found id: " + id))
                ).map(label -> toEntity(label, rq))
                .orElseGet(() ->
                        repository.save(toEntity(new Label(), rq))
                );
    }

    /**
     * Converts {@link LabelRequest} to {@link Label} object.
     * <p>
     * Example:
     * <pre>
     *     toEntity(new Label(), rq);
     * </pre>
     *
     * @param label {@link Label} object for save to database or update existing
     * @param rq {@link LabelRequest} object for converting to {@link Label}
     * @return {@link Label} saved object
     */
    private Label toEntity(Label label, LabelRequest rq) {
        label.setName(rq.name());
        label.setIsArchived(rq.isArchived());
        return label;
    }
}