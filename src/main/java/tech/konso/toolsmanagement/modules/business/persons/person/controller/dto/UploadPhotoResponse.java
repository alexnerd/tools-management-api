package tech.konso.toolsmanagement.modules.business.persons.person.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO class for response to return saved photo uuid.
 *
 * @param uuid UUID of person photo in file storage
 */
@Schema(description = "Response for return saved photo UUID of person in file storage")
public record UploadPhotoResponse(
        @Schema(description = "UUID of person photo in file storage", example = "3d965e4e-cf28-45e1-91c7-1225566e6817")
        UUID uuid) {
}
