package tech.konso.toolsmanagement.modules.business.persons.label.service.mappers;

import tech.konso.toolsmanagement.modules.business.persons.label.controller.dto.LabelRequest;
import tech.konso.toolsmanagement.modules.business.persons.label.persistence.dao.Label;

/**
 * Mapper for label entity
 */

public class LabelEntityMapper {

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
    public Label toEntity(Label label, LabelRequest rq) {
        label.setName(rq.name());
        label.setIsArchived(rq.isArchived());
        return label;
    }
}
