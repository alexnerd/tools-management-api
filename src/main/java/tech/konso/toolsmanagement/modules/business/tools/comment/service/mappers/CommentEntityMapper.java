package tech.konso.toolsmanagement.modules.business.tools.comment.service.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentRequest;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.dao.Comment;
import tech.konso.toolsmanagement.modules.business.tools.tool.service.ToolService;

@Service
public class CommentEntityMapper {
    @Autowired
    private ToolService toolService;

    public Comment toEntity(Comment comment, CommentRequest rq) {
        comment.setTool(toolService.getReference(rq.toolId()));
        comment.setContent(rq.content());
        comment.setPersonUuid(rq.personUuid());
        return comment;
    }

    public Comment updateContent(Comment comment, CommentRequest rq) {
        comment.setContent(rq.content());
        return comment;
    }
}
