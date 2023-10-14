package tech.konso.toolsmanagement.modules.persons.business.person.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Person info DTO class for {@link PersonFilterResponse}
 * used in response for find persons by parameters
 *
 * @param id             person id
 * @param uuid           person business key
 * @param phoneNumber    person phone number
 * @param companyUuid    business key from Module Projects - Project company
 * @param surname        person surname
 * @param name           person name
 * @param patronymic     person patronymic
 * @param jobTitle       person job title
 * @param isArchived     archived flag
 * @param isUnregistered unregistered flag
 * @param labels         set of labels names
 * @param roles          set of roles names
 * @param createdAt      create date
 * @param updatedAt      update date
 */

@Builder
@Schema(description = "Person information with simplified inner objects" +
        "(just returns names for inner objects or other simple information to display)")
public record PersonFilterInfo(@Schema(description = "person id", example = "1")
                               Long id,
                               @Schema(description = "person business key", example = "3776a6ee-c40e-4401-b8c8-2f96f49d0c41")
                               UUID uuid,
                               @Schema(description = "person phone number", example = "7-904-357-2233", nullable = true)
                               String phoneNumber,
                               @Schema(description = "business key from Module Projects - Project company", example = "3d965e4e-cf28-45e1-91c7-1225566e6811", nullable = true)
                               UUID companyUuid,
                               @Schema(description = "person surname", example = "Smith")
                               String surname,
                               @Schema(description = "person name", example = "Alex")
                               String name,
                               @Schema(description = "person patronymic", example = "Petrovich", nullable = true)
                               String patronymic,
                               @Schema(description = "person job title", example = "Foreman")
                               String jobTitle,
                               @Schema(description = "archived flag", example = "false")
                               Boolean isArchived,
                               @Schema(description = "unregistered flag", example = "false")
                               Boolean isUnregistered,
                               @Schema(description = "roles name", example = "[\"Admin\",\"SuperUser\"]")
                               Set<String> roles,
                               @Schema(description = "labels names", example = "[\"Attention\",\"Important\"]")
                               Set<String> labels,
                               @Schema(description = "create date", example = "2023-08-13T18:05:29.179615")
                               LocalDateTime createdAt,
                               @Schema(description = "update date", example = "2023-08-13T18:05:29.179615")
                               LocalDateTime updatedAt) {
}
