package tech.konso.toolsmanagement.modules.business.tools.comment.service.mappers;

import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentFilterInfo;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.dao.Comment;

/**
 * Mapper class Comment entity -> CommentFilterInfo
 */
public class CommentDtoMapper {

    /**
     * Converts {@link Comment} entity to {@link CommentFilterInfo} object.
     * <p>
     * Example:
     * <pre>
     *     toCommentFilterInfo(new Comment());
     * </pre>
     *
     * @param comment {@link Comment} object for mapping to DTO
     * @return {@link CommentFilterInfo} mapped object
     */
    public CommentFilterInfo toCommentFilterInfo(Comment comment) {
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
