package tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Short person description DTO class
 * used to describe person by basic fields
 *
 * @param id         label id
 * @param name       person name
 * @param surname    person surname
 * @param jobTitle   person job title
 * @param isArchived person is archived flag
 */

@Builder
@Schema(description = "Short person description DTO class used to describe person by basic fields")
public record PersonShort(@Schema(description = "person id", example = "1")
                          Long id,
                          @Schema(description = "person name", example = "Jim")
                          String name,
                          @Schema(description = "person surname", example = "Morrison")
                          String surname,
                          @Schema(description = "person job title", example = "Worker")
                          String jobTitle,
                          @Schema(description = "person is archived flag", example = "false")
                          Boolean isArchived) {
}
