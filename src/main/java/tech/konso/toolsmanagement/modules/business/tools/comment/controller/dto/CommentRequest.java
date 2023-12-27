package tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

/**
 * DTO class for request to save new comment or updating existing comment.
 *
 * @param id         of comment
 * @param toolId     commented tool id, must not be null
 * @param content    of the comment, must not be blank
 * @param personUuid person uuid of the comment, must not be null
 */

@Schema(description = "Request to save new comment or updating existing comment")
@Builder
public record CommentRequest(
        @Schema(description = "comment id, if comment is null then new comment will be saved, " +
                "if id is not null, then existing comment will update", example = "4") Long id,
        @Schema(description = "commented tool id", example = "1")
        @NotNull(message = "Tool id must not be null") Long toolId,
        @Schema(description = "comment content", example = "So good tool")
        @NotBlank(message = "Tool comment must not be blank or empty") String content,
        @Schema(description = "person uuid of the comment", example = "935921a7-692e-4ee4-a089-2695b68e9804")
        @NotNull(message = "Person UUID must not be null") UUID personUuid) {
}
