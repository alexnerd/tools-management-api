package tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto;

import lombok.Builder;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.dao.enums.OwnershipType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Tool info DTO class for get info by id API
 *
 * @param id              tool id
 * @param uuid            tool business key
 * @param name            of the tool, must not be blank
 * @param isConsumable    consumable mark, must not be null
 * @param brand           brand name
 * @param inventoryNumber tool inventory number, may be null or must not be blank
 * @param responsible     responsible person name
 * @param category        category name
 * @param project         project name
 * @param price           tool price
 * @param ownershipType   type of ownership
 * @param rentTill        last day of tool rent
 * @param isKit           flag is the tool a kit, must not be null
 * @param kitUuid         kit id
 * @param labels          set of labels ids, must not be null
 * @param isArchived      flag, must not be null
 * @param createdAt       create date
 * @param updatedAt       update date
 */

@Builder
public record ToolInfo(Long id, UUID uuid, String name, Boolean isConsumable, BrandShort brand, String inventoryNumber,
                       String responsible, CategoryShort category, String project, BigDecimal price, OwnershipType ownershipType,
                       LocalDate rentTill, Boolean isKit, UUID kitUuid, Set<LabelShort> labels, Boolean isArchived,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
}