package tech.konso.toolsmanagement.modules.business.tools.comment.service.mappers;

import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentFilterInfo;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.dao.Comment;

public class CommentDtoMapper {
    public CommentFilterInfo mapToCommentFilterInfo(Comment comment) {
        return CommentFilterInfo.builder()
                .id(comment.getId())
                .content(comment.getContent())
                //TODO: change after adding persons module
                .person(null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
