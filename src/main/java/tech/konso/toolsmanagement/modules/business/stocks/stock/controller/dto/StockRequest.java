package tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

/**
 * DTO class for request to save new stock or updating existing stock.
 *
 * @param id                     of stock, may be null
 * @param name                   stock name, must not be blank
 * @param address                stock address, must not be blank
 * @param companyUuid            business key from Module Projects - Project company
 * @param responsibleCompanyUuid business key from Module Projects - Project company
 * @param responsiblePersonUuid  business key from Module Persons - Person person
 * @param isArchived             flag, must not be null
 */

@Builder
@Schema(description = "Request to save new stock or updating existing stock")
public record StockRequest(
        @Schema(description = "stock id, if stock is null then new stock will be saved, " +
                "if id is not null, then existing stock will update", example = "4")
        Long id,
        @Schema(description = "stock name", example = "Alex")
        @NotBlank String name,
        @Schema(description = "stock address", example = "Moscow, Petroka 56")
        @NotBlank String address,
        @Schema(description = "business key from Module Projects - Project company", example = "3d965e4e-cf28-45e1-91c7-1225566e6811", nullable = true)
        UUID companyUuid,
        @Schema(description = "business key from Module Projects - Project company", example = "3d965e4e-cf28-45e1-91c7-1225566e6811", nullable = true)
        UUID responsibleCompanyUuid,
        @Schema(description = "business key from Module Persons - Person person", example = "3d965e4e-cf28-45e1-91c7-1225566e6811", nullable = true)
        UUID responsiblePersonUuid,
        @Schema(description = "archived flag, must not be null", example = "false")
        @NotNull Boolean isArchived) {
}
