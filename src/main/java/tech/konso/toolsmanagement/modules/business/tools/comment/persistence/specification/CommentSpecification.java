package tech.konso.toolsmanagement.modules.business.tools.comment.persistence.specification;

import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.domain.Specification;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.dao.Comment;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.dao.Comment_;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.Tool_;
import tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification;

import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.FILTER_SEPARATOR;

/**
 * Class with specifications for {@link Comment}
 */
public final class CommentSpecification {

    /**
     * Specification for commented tool.
     *
     * @param toolId id of commented tool
     * @return created specification
     */
    public static Specification<Comment> toolSpec(Long toolId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Comment_.TOOL)
                        .get(Tool_.ID), toolId);
    }

    /**
     * Specification for sorting comments.
     * Support sorting by create date, update date.
     * For every filter supports asc and desc order.
     * To specify order you must specify it using {@link AbstractSpecification#FILTER_SEPARATOR}
     * <p>
     * Example:
     * <pre>
     *     updatedat,desc
     * </pre>
     * <p>
     * By default, sorts by create date in desc order.
     *
     * @param sort filed name. Can be: createdat, updatedat.
     * @return created specification
     */
    public static Specification<Comment> sortSpec(String sort) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Order descCreateDate = criteriaBuilder.desc(root.get(Comment_.CREATED_AT));

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
                case "createdat":
                    if (asc) criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Comment_.CREATED_AT)));
                    else criteriaQuery.orderBy(descCreateDate);
                    break;
                case "updatedat":
                    if (asc) criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Comment_.UPDATED_AT)));
                    else criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Comment_.UPDATED_AT)));
                    break;

                default:
                    criteriaQuery.orderBy(descCreateDate);
            }
            return null;
        };
    }
}
