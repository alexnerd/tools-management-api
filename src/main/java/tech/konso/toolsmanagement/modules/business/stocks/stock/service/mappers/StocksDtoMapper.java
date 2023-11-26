package tech.konso.toolsmanagement.modules.business.stocks.stock.service.mappers;

import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockFilterInfo;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockInfo;
import tech.konso.toolsmanagement.modules.business.stocks.stock.persistence.dao.Stock;

public class StocksDtoMapper {

    public StockFilterInfo mapToStockFilterInfo(Stock stock) {
        return StockFilterInfo.builder()
                .id(stock.getId())
                .uuid(stock.getUuid())
                .name(stock.getName())
                .address(stock.getAddress())
                .companyUuid(stock.getCompanyUuid())
                .responsibleCompanyUuid(stock.getResponsibleCompanyUuid())
                .responsiblePersonUuid(stock.getResponsiblePersonUuid())
                .isArchived(stock.getIsArchived())
                .createdAt(stock.getCreatedAt())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }
    
    public StockInfo mapToStockInfo(Stock stock) {
        return StockInfo.builder()
                .id(stock.getId())
                .uuid(stock.getUuid())
                .name(stock.getName())
                .address(stock.getAddress())
                .companyUuid(stock.getCompanyUuid())
                .responsibleCompanyUuid(stock.getResponsibleCompanyUuid())
                .responsiblePersonUuid(stock.getResponsiblePersonUuid())
                .isArchived(stock.getIsArchived())
                .createdAt(stock.getCreatedAt())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }
}
