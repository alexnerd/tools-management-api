package tech.konso.toolsmanagement.modules.business.stocks.stock.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockFilterInfo;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockFilterResponse;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockInfo;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockRequest;
import tech.konso.toolsmanagement.modules.business.stocks.stock.persistence.dao.Stock;
import tech.konso.toolsmanagement.modules.business.stocks.stock.persistence.repository.StockRepository;
import tech.konso.toolsmanagement.modules.business.stocks.stock.persistence.specification.StockSpecification;
import tech.konso.toolsmanagement.modules.business.stocks.stock.service.mappers.StocksDtoMapper;
import tech.konso.toolsmanagement.modules.business.stocks.stock.service.mappers.StocksEntityMapper;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;
import tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification;

import java.util.Optional;

import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

/**
 * Service layer for working with stocks.
 */
@Service
public class StockService {

    @Autowired
    private StockRepository repository;

    @Autowired
    private StocksEntityMapper entityMapper;

    private StocksDtoMapper stocksDtoMapper;

    @PostConstruct
    public void init() {
        stocksDtoMapper = new StocksDtoMapper();
    }

    /**
     * Find stock in database by unique id. Stock must exist in database
     * <p>
     * Example:
     * <pre>
     *     Stock stock = findById(2L);
     * </pre>
     *
     * @param id of stock, must exist in database
     * @return stock from database
     * @throws BPException if stock not exists in database
     */
    public StockInfo findById(Long id) {
        return repository.findById(id).map(stocksDtoMapper::mapToStockInfo).orElseThrow(() ->
                new BPException.NotFound("Stock not found id: " + id));
    }

    /**
     * Finds stocks by stock specification and returns it in pageable format.
     * By default, result set sorts by create date from newer to older and without archived stocks.
     * <p>
     * Example:
     * <pre>
     *     Specification<Stock> spec = specBuilder(sortSpec("name,desc")).build();
     *     StockFilterResponse foundedStocks = service.findAll(0, 100, spec);
     * </pre>
     *
     * @param page number of returned result set
     * @param size of the returned page
     * @param spec set of stock specification
     * @return {@link StockFilterResponse} object for resulting dataset in pageable format
     * @see StockSpecification stock specifications
     */
    public Page<StockFilterInfo> findAll(int page, int size, Specification<Stock> spec) {
        AbstractSpecification.SpecBuilder<Stock> builder = specBuilder(Stock.class);
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(builder.and(spec).build(), pageable).map(stocksDtoMapper::mapToStockFilterInfo);
    }

    /**
     * Save new stock to database or update existing.
     * Run under transaction.
     * <p>
     * Example:
     * <pre>
     *     StockRequest rq = new StockRequest(null, "new_stock", "adress", null, null, null, false);
     *     Stock savedStock = service.save(rq);
     * </pre>
     *
     * @param rq {@link StockRequest} object for creating stock
     * @return {@link Stock} saved object
     */
    @Transactional
    public Stock save(StockRequest rq) {
        return Optional.ofNullable(rq.id())
                .map(id -> repository.findById(rq.id())
                        .orElseThrow(() -> new BPException.NotFound("Stock not found id: " + id))
                ).map(stock -> entityMapper.toEntity(stock, rq))
                .orElseGet(() ->
                        repository.save(entityMapper.toEntity(new Stock(), rq))
                );
    }
}
