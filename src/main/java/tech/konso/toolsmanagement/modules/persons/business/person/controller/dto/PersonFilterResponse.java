package tech.konso.toolsmanagement.modules.persons.business.person.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO class for response to return result set of persons in pageable format.
 *
 * @param persons    list of found persons. List size limited by page size
 * @param totalItems total number of persons found
 */

@Schema(description = "Response for return result set of persons in pageable format")
public record PersonFilterResponse(@Schema(description = "list of found persons, list size limited by page size")
                                   List<PersonFilterInfo> persons,
                                   @Schema(description = "total number of persons found", example = "20")
                                   Long totalItems) {
}

