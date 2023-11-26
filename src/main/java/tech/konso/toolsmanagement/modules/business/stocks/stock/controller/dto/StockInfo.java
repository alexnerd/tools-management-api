package tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * Stock info DTO class for get info by id API
 *
 * @param id          stock id
 * @param uuid        stock business key
 * @param name        stock name
 * @param address     stock address
 * @param companyUuid business key from Module Projects - Project company
 * @param responsibleCompanyUuid business key from Module Projects - Project company
 * @param responsiblePersonUuid business key from Module Persons - Person person
 * @param isArchived  archived flag
 * @param createdAt   create date
 * @param updatedAt   update date
 */

@Builder
@Schema(description = "Stock full information")
public record StockInfo(@Schema(description = "stock id", example = "1")
                        Long id,
                        @Schema(description = "stock business key", example = "3776a6ee-c40e-4401-b8c8-2f96f49d0c41")
                        UUID uuid,
                        @Schema(description = "stock name", example = "Alex")
                        String name,
                        @Schema(description = "stock address", example = "Moscow, Petroka 56")
                        String address,
                        @Schema(description = "business key from Module Projects - Project company", example = "3d965e4e-cf28-45e1-91c7-1225566e6811", nullable = true)
                        UUID companyUuid,
                        @Schema(description = "business key from Module Projects - Project company", example = "3d965e4e-cf28-45e1-91c7-1225566e6811", nullable = true)
                        UUID responsibleCompanyUuid,
                        @Schema(description = "business key from Module Persons - Person person", example = "3d965e4e-cf28-45e1-91c7-1225566e6811", nullable = true)
                        UUID responsiblePersonUuid,
                        @Schema(description = "archived flag", example = "false")
                        Boolean isArchived,
                        @Schema(description = "create date", example = "2023-08-13T18:05:29.179615")
                        LocalDateTime createdAt,
                        @Schema(description = "update date", example = "2023-08-13T18:05:29.179615")
                        LocalDateTime updatedAt) {
}

