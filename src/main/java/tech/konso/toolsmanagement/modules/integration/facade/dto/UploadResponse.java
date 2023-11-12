package tech.konso.toolsmanagement.modules.integration.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO class for map file upload response from file storage service.
 *
 * @param uuid  file from file storage
 * @param error info from file storage
 */
@Schema(description = "Upload file response from file storage service")
public record UploadResponse(
        @Schema(description = "UUID of photo in file storage", example = "3d965e4e-cf28-45e1-91c7-1225566e6817")
        UUID uuid,
        @Schema(description = "Error message from file storage service", example = "File not found")
        String error) {
}
