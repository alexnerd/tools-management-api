package tech.konso.toolsmanagement.modules.persons.business.role.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tech.konso.toolsmanagement.modules.persons.business.role.persistence.dao.Role;

import java.util.List;

/**
 * DTO class for response to return result set of roles in pageable format.
 *
 * @param roles     list of found roles. List size limited by page size
 * @param totalItems total number of roles found
 */

@Schema(description = "Response for return result set of roles in pageable format")
public record RoleFilterResponse(@Schema(description = "list of found roles, list size limited by page size")
                                  List<Role> roles,
                                  @Schema(description = "total number of roles found", example = "20")
                                  Long totalItems) {
}

