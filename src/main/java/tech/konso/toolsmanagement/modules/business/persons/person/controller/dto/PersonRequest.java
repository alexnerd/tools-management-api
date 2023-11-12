package tech.konso.toolsmanagement.modules.business.persons.person.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

/**
 * DTO class for request to save new person or updating existing person.
 *
 * @param id             of person, may be null
 * @param phoneNumber    person phone number
 * @param companyUuid    business key from Module Projects - Project company
 * @param surname        person surname, must not be blank
 * @param name           person name, must not be blank
 * @param patronymic     person patronymic, may be null
 * @param jobTitle       person job title, must not be blank
 * @param isArchived     flag, must not be null
 * @param isUnregistered flag, must not be null
 * @param photoUuid      UUID of person photo in file storage
 * @param labels         set of labels ids, must not be null
 * @param roles          set of roles ids, must not be null
 */

@Builder
@Schema(description = "Request to save new person or updating existing person")
public record PersonRequest(
        @Schema(description = "person id, if person is null then new person will be saved, " +
                "if id is not null, then existing person will update", example = "4")
        Long id,
        @Schema(description = "person phone number", example = "7-904-357-2233", nullable = true)
        String phoneNumber,
        @Schema(description = "business key from Module Projects - Project company", example = "3d965e4e-cf28-45e1-91c7-1225566e6811", nullable = true)
        UUID companyUuid,
        @Schema(description = "person surname", example = "Smith")
        @NotBlank String surname,
        @Schema(description = "person name", example = "Alex")
        @NotBlank String name,
        @Schema(description = "person patronymic", example = "Petrovich", nullable = true)
        String patronymic,
        @Schema(description = "person job title", example = "Foreman")
        @NotBlank String jobTitle,
        @Schema(description = "archived flag, must not be null", example = "false")
        @NotNull Boolean isArchived,
        @Schema(description = "unregistered flag, must not be null", example = "false")
        @NotNull Boolean isUnregistered,
        @Schema(description = "UUID of person photo in file storage", example = "eee7339f-4977-4186-bb5a-3a73585efe65")
        UUID photoUuid,
        @Schema(description = "label ids", example = "[19, 38]")
        @NotNull Set<Long> labels,
        @Schema(description = "roles ids", example = "[19, 38]")
        @NotNull Set<Long> roles) {
}