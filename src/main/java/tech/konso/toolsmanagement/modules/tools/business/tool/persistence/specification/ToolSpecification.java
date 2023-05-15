package tech.konso.toolsmanagement.modules.tools.business.tool.persistence.specification;

import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.domain.Specification;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.dao.Tool;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.dao.Tool_;

import static tech.konso.toolsmanagement.modules.tools.commons.AbstractSpecification.FILTER_SEPARATOR;
import static tech.konso.toolsmanagement.modules.tools.commons.AbstractSpecification.LIKE_NAME_MIN_LENGTH;

/**
 * Class with specifications for {@link Tool}
 */
public final class ToolSpecification {

    /**
     * Specification for name.
     * Uses like pattern %name%.
     * Before searching transform string to lower case.
     * Working only with string greater or equals {@link tech.konso.toolsmanagement.modules.tools.commons.AbstractSpecification#LIKE_NAME_MIN_LENGTH
     *
     * @param likeName searching string
     * @return created specification
     */
    public static Specification<Tool> likeSpec(String likeName) {
        if (likeName == null || likeName.isBlank()) return null;
        if (likeName.length() < LIKE_NAME_MIN_LENGTH) return null;

        String likePattern = '%' + likeName.toLowerCase() + '%';
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get(Tool_.NAME)), likePattern);
    }

    /**
     * Specification for archive flag.
     *
     * @param isArchived flag
     * @return created specification
     */
    public static Specification<Tool> isArchivedSpec(boolean isArchived) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Tool_.IS_ARCHIVED), isArchived);
    }

    /**
     * Specification for sorting tools.
     * Support sorting by name, create date, update date.
     * For every filter supports asc and desc order.
     * To specify order you must specify it using {@link tech.konso.toolsmanagement.modules.tools.commons.AbstractSpecification#FILTER_SEPARATOR}
     * <p>
     * Example:
     * <pre>
     *     name,desc
     * </pre>
     * <p>
     * By default, sorts by create date in desc order.
     *
     * @param sort filed name. Can be: name, createdat, updatedat.
     * @return created specification
     */
    public static Specification<Tool> sortSpec(String sort) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Order descCreateDate = criteriaBuilder.desc(root.get(Tool_.CREATED_AT));

            if (sort == null) {
                criteriaQuery.orderBy(descCreateDate);
                return null;
            }

            String field = sort;
            boolean asc = true;
            if (sort.contains(FILTER_SEPARATOR)) {
                String[] split = sort.split(FILTER_SEPARATOR);
                field = split[0];
                asc = !"desc".equalsIgnoreCase(split[1]);
            }

            switch (field.toLowerCase()) {
                case "name":
                    if (asc) criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Tool_.NAME)));
                    else criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Tool_.NAME)));
                    break;
                case "createdat":
                    if (asc) criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Tool_.CREATED_AT)));
                    else criteriaQuery.orderBy(descCreateDate);
                    break;
                case "updatedat":
                    if (asc) criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Tool_.UPDATED_AT)));
                    else criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Tool_.UPDATED_AT)));
                    break;

                default:
                    criteriaQuery.orderBy(descCreateDate);
            }
            return null;
        };
    }
}
