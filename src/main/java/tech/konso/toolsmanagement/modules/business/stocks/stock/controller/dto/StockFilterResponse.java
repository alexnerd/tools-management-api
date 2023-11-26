package tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO class for response to return result set of stocks in pageable format.
 *
 * @param stocks    list of found stocks. List size limited by page size
 * @param totalItems total number of stocks found
 */

@Schema(description = "Response for return result set of stocks in pageable format")
public record StockFilterResponse(@Schema(description = "list of found stocks, list size limited by page size")
                                   List<StockFilterInfo> stocks,
                                   @Schema(description = "total number of stocks found", example = "20")
                                   Long totalItems) {
}
