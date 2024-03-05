package tech.konso.toolsmanagement.modules.business.tools.comment.service.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentRequest;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.dao.Comment;
import tech.konso.toolsmanagement.modules.business.tools.tool.service.ToolService;

/**
 * Mapper for comment entity
 */
@Service
public class CommentEntityMapper {
    @Autowired
    private ToolService toolService;

    /**
     * Converts {@link CommentRequest} to {@link Comment} entity.
     * <p>
     * Example:
     * <pre>
     *     toEntity(new Comment(), rq);
     * </pre>
     *
     * @param comment {@link Comment} object for save to database
     * @param rq      {@link CommentRequest} object for converting to {@link Comment}
     * @return {@link Comment} saved entity
     */
    public Comment toEntity(Comment comment, CommentRequest rq) {
        comment.setTool(toolService.getReference(rq.toolId()));
        comment.setContent(rq.content());
        comment.setPersonUuid(rq.personUuid());
        return comment;
    }

    /**
     * Save data from {@link CommentRequest} to {@link Comment} entity.
     * <p>
     * Example:
     * <pre>
     *     updateEntity(new Comment(), rq);
     * </pre>
     *
     * @param comment {@link Comment} object for update from database
     * @param rq      {@link CommentRequest} object for converting to {@link Comment}
     * @return {@link Comment} updated entity
     */
    public Comment updateEntity(Comment comment, CommentRequest rq) {
        comment.setContent(rq.content());
        return comment;
    }
}
