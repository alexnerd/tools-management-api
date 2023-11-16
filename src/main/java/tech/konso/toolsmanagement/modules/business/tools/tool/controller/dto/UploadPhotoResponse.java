package tech.konso.toolsmanagement.modules.business.tools.tool.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO class for response to return saved photo uuid.
 *
 * @param uuid Tool photo UUID in file storage
 */
@Schema(description = "Response for return saved tool photo UUID in file storage")
public record UploadPhotoResponse(
        @Schema(description = "Tool photo UUID in file storage", example = "3d965e4e-cf28-45e1-91c7-1225566e6817")
        UUID uuid) {
}
