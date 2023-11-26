package tech.konso.toolsmanagement.modules.business.stocks.stock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockFilterInfo;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockFilterResponse;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockInfo;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockRequest;
import tech.konso.toolsmanagement.modules.business.stocks.stock.persistence.dao.Stock;
import tech.konso.toolsmanagement.modules.business.stocks.stock.service.StockService;

import java.net.URI;

import static tech.konso.toolsmanagement.modules.business.stocks.stock.persistence.specification.StockSpecification.*;
import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

@Slf4j
@Validated
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 1800L,
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT})
@RequestMapping("/v1/stocks/stock")
@Tag(name = "stocks-resource", description = "API resource for management stocks")
public class StockController {
    @Autowired
    private StockService service;

    @Operation(summary = "Get stock by id")
    @Parameters({
            @Parameter(name = "id", description = "id of stock to be searched", example = "7", required = true)
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StockInfo find(@PathVariable("id") Long id) {
        return service.findById(id);
    }


    @Operation(summary = "List stocks by filter")
    @Parameters({
            @Parameter(name = "page", description = "page number of result dataset, min value is 1", example = "1", required = true),
            @Parameter(name = "size", description = "size of result dataset page, min value is 1, max value is 50", example = "20", required = true),
            @Parameter(name = "name", description = "name of stock, min length is 3", example = "Tool stock"),
            @Parameter(name = "isArchived", description = "Archived flag, false by default", example = "false"),
            @Parameter(name = "sort", description = "Sorting filter supports: name(stock name), createdat(created date), updatedat(updated date)." +
                    " Every filter supports asc and desc order. By default sorts by create date in desc order. " +
                    "To choose sorting order type filter name and, by comma separator, order (asd, desc)", example = "name,asc")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public StockFilterResponse findAll(@RequestParam(value = "page") @Min(1) int page,
                                       @RequestParam(value = "size") @Min(1) @Max(50) int size,
                                       @RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "isArchived", required = false, defaultValue = "false") Boolean isArchived,
                                       @RequestParam(value = "sort", required = false) String sort) {
        // UI pages starts with 1
        Page<StockFilterInfo> stocks = service.findAll(page - 1, size, specBuilder(isArchivedSpec(isArchived)
                .and(likeSpec(name))
                .and(sortSpec(sort)))
                .build());
        return new StockFilterResponse(stocks.getContent(), stocks.getTotalElements());
    }

    @Operation(summary = "Update existing stock by id")
    @Parameters({
            @Parameter(name = "rq", description = "Request body fo update stock", required = true,
                    schema = @Schema(implementation = StockRequest.class))
    })
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@Valid @RequestBody StockRequest rq) {
        service.save(rq);
        return ResponseEntity.noContent().build();
    }

    @Parameters({
            @Parameter(name = "rq", description = "Request body for save new stock", required = true,
                    schema = @Schema(implementation = StockRequest.class))
    })
    @Operation(summary = "Create stock")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@Valid @RequestBody StockRequest rq) {
        Stock stock = service.save(rq);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(stock.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }
}
