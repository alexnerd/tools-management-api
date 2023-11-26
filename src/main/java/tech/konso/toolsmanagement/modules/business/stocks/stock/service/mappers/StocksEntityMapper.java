package tech.konso.toolsmanagement.modules.business.stocks.stock.service.mappers;

import org.springframework.stereotype.Service;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockRequest;
import tech.konso.toolsmanagement.modules.business.stocks.stock.persistence.dao.Stock;

import java.util.UUID;

/**
 * Mapper for stock entity
 */
@Service
public class StocksEntityMapper {
    /**
     * Converts {@link StockRequest} to {@link Stock} object.
     * <p>
     * Example:
     * <pre>
     *     toEntity(new Stock(), rq);
     * </pre>
     *
     * @param stock {@link Stock} object for save to database or update existing
     * @param rq {@link StockRequest} object for converting to {@link Stock}
     * @return {@link Stock} saved object
     */
    public Stock toEntity(Stock stock, StockRequest rq) {
        if (stock.getId() == null) {
            stock.setUuid(UUID.randomUUID());
        }
        stock.setName(rq.name());
        stock.setAddress(rq.address());
        stock.setCompanyUuid(rq.companyUuid());
        stock.setResponsibleCompanyUuid(rq.responsibleCompanyUuid());
        stock.setResponsiblePersonUuid(rq.responsiblePersonUuid());
        stock.setIsArchived(rq.isArchived());

        return stock;
    }
}
