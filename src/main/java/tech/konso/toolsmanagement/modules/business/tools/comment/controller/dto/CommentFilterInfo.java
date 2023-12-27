package tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Comment info DTO class for {@link CommentFilterResponse}
 * used in response for find comments by parameters
 *
 * @param id        tool id
 * @param content   of the tool, must not be blank
 * @param person    short person description
 * @param createdAt create date
 * @param updatedAt update date
 */

@Builder
@Schema(description = "Comment information with simplified inner objects" +
        "(just returns names for inner objects or other simple information to display)")
public record CommentFilterInfo(@Schema(description = "comment id", example = "1")
                                Long id,
                                @Schema(description = "comment content", example = "nice tool")
                                String content,
                                @Schema(description = "short person description")
                                PersonShort person,
                                @Schema(description = "create date", example = "2023-08-13T18:05:29.179615")
                                LocalDateTime createdAt,
                                @Schema(description = "update date", example = "2023-08-13T18:05:29.179615")
                                LocalDateTime updatedAt) {
}
