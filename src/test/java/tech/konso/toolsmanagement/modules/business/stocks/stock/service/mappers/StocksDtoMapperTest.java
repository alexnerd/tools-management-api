package tech.konso.toolsmanagement.modules.business.stocks.stock.service.mappers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockFilterInfo;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockInfo;
import tech.konso.toolsmanagement.modules.business.stocks.stock.persistence.dao.Stock;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for StocksDtoMapper. Test for mapping fields and null values.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StocksDtoMapperTest {

    private StocksDtoMapper mapper;

    @BeforeAll
    public void init() {
        mapper = new StocksDtoMapper();
    }

    @Nested
    class StockFilterInfoMapperTests {

        /**
         * {@link StocksDtoMapper#mapToStockFilterInfo(Stock)} should map {@link Stock} uuid field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockFilterInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_filter_info_should_map_uuid() {
            UUID uuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
            Stock stock = new Stock();
            stock.setUuid(uuid);

            StockFilterInfo stockFilterInfo = mapper.mapToStockFilterInfo(stock);

            assertEquals(uuid, stockFilterInfo.uuid());
        }

        /**
         * {@link StocksDtoMapper#mapToStockFilterInfo(Stock)} should map {@link Stock} name field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockFilterInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_filter_info_should_map_name() {
            String name = "Food stock";
            Stock stock = new Stock();
            stock.setName(name);

            StockFilterInfo stockFilterInfo = mapper.mapToStockFilterInfo(stock);

            assertEquals(name, stockFilterInfo.name());
        }

        /**
         * {@link StocksDtoMapper#mapToStockFilterInfo(Stock)} should map {@link Stock} address field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockFilterInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_filter_info_should_map_address() {
            String address = "Adress";
            Stock stock = new Stock();
            stock.setAddress(address);

            StockFilterInfo stockFilterInfo = mapper.mapToStockFilterInfo(stock);

            assertEquals(address, stockFilterInfo.address());
        }

        /**
         * {@link StocksDtoMapper#mapToStockFilterInfo(Stock)} should map {@link Stock} companyUuid field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockFilterInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_filter_info_should_map_company_uuid() {
            UUID companyUuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
            Stock stock = new Stock();
            stock.setCompanyUuid(companyUuid);

            StockFilterInfo stockFilterInfo = mapper.mapToStockFilterInfo(stock);

            assertEquals(companyUuid, stockFilterInfo.companyUuid());
        }

        /**
         * {@link StocksDtoMapper#mapToStockFilterInfo(Stock)} should map {@link Stock} responsibleCompanyUuid field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockFilterInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_filter_info_should_map_responsible_company_uuid() {
            UUID companyUuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
            Stock stock = new Stock();
            stock.setResponsibleCompanyUuid(companyUuid);

            StockFilterInfo stockFilterInfo = mapper.mapToStockFilterInfo(stock);

            assertEquals(companyUuid, stockFilterInfo.responsibleCompanyUuid());
        }

        /**
         * {@link StocksDtoMapper#mapToStockFilterInfo(Stock)} should map {@link Stock} responsiblePersonUuid field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockFilterInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_filter_info_should_map_responsible_person_uuid() {
            UUID personUuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
            Stock stock = new Stock();
            stock.setResponsiblePersonUuid(personUuid);

            StockFilterInfo stockFilterInfo = mapper.mapToStockFilterInfo(stock);

            assertEquals(personUuid, stockFilterInfo.responsiblePersonUuid());
        }

        /**
         * {@link StocksDtoMapper#mapToStockFilterInfo(Stock)} should map {@link Stock} isArchived field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockFilterInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_filter_info_should_map_is_archived() {
            Boolean isArchived = Boolean.TRUE;
            Stock stock = new Stock();
            stock.setIsArchived(isArchived);

            StockFilterInfo stockFilterInfo = mapper.mapToStockFilterInfo(stock);

            assertEquals(isArchived, stockFilterInfo.isArchived());
        }

        /**
         * {@link StocksDtoMapper#mapToStockFilterInfo(Stock)} should map {@link Stock} createdAt field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockFilterInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_filter_info_should_map_created_at() {
            LocalDateTime createdAt = LocalDateTime.now();
            Stock stock = new Stock();
            stock.setCreatedAt(createdAt);

            StockFilterInfo stockFilterInfo = mapper.mapToStockFilterInfo(stock);

            assertEquals(createdAt, stockFilterInfo.createdAt());
        }

        /**
         * {@link StocksDtoMapper#mapToStockFilterInfo(Stock)} should map {@link Stock} updatedAt field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockFilterInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_filter_info_should_map_updated_at() {
            LocalDateTime updatedAt = LocalDateTime.now();
            Stock stock = new Stock();
            stock.setUpdatedAt(updatedAt);

            StockFilterInfo stockFilterInfo = mapper.mapToStockFilterInfo(stock);

            assertEquals(updatedAt, stockFilterInfo.updatedAt());
        }
    }

    @Nested
    class StockInfoMapperTests {
        /**
         * {@link StocksDtoMapper#mapToStockInfo(Stock)} should map {@link Stock} uuid field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_info_should_map_uuid() {
            UUID uuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
            Stock stock = new Stock();
            stock.setUuid(uuid);

            StockInfo stockInfo = mapper.mapToStockInfo(stock);

            assertEquals(uuid, stockInfo.uuid());
        }

        /**
         * {@link StocksDtoMapper#mapToStockInfo(Stock)} should map {@link Stock} name field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_info_should_map_name() {
            String name = "Food stock";
            Stock stock = new Stock();
            stock.setName(name);

            StockInfo stockInfo = mapper.mapToStockInfo(stock);

            assertEquals(name, stockInfo.name());
        }

        /**
         * {@link StocksDtoMapper#mapToStockInfo(Stock)} should map {@link Stock} address field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_info_should_map_address() {
            String address = "Food stock";
            Stock stock = new Stock();
            stock.setAddress(address);

            StockInfo stockInfo = mapper.mapToStockInfo(stock);

            assertEquals(address, stockInfo.address());
        }

        /**
         * {@link StocksDtoMapper#mapToStockInfo(Stock)} should map {@link Stock} companyUuid field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_info_should_map_company_uuid() {
            UUID companyUuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
            Stock stock = new Stock();
            stock.setCompanyUuid(companyUuid);

            StockInfo stockInfo = mapper.mapToStockInfo(stock);

            assertEquals(companyUuid, stockInfo.companyUuid());
        }

        /**
         * {@link StocksDtoMapper#mapToStockInfo(Stock)} should map {@link Stock} responsibleCompanyUuid field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_info_should_map_responsible_company_uuid() {
            UUID companyUuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
            Stock stock = new Stock();
            stock.setResponsibleCompanyUuid(companyUuid);

            StockInfo stockInfo = mapper.mapToStockInfo(stock);

            assertEquals(companyUuid, stockInfo.responsibleCompanyUuid());
        }

        /**
         * {@link StocksDtoMapper#mapToStockInfo(Stock)} should map {@link Stock} responsiblePersonUuid field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_info_should_map_responsible_person_uuid() {
            UUID personUuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
            Stock stock = new Stock();
            stock.setResponsiblePersonUuid(personUuid);

            StockInfo stockInfo = mapper.mapToStockInfo(stock);

            assertEquals(personUuid, stockInfo.responsiblePersonUuid());
        }

        /**
         * {@link StocksDtoMapper#mapToStockInfo(Stock)} should map {@link Stock} isArchived field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_info_should_map_is_archived() {
            Boolean isArchived = Boolean.TRUE;
            Stock stock = new Stock();
            stock.setIsArchived(isArchived);

            StockInfo stockInfo = mapper.mapToStockInfo(stock);

            assertEquals(isArchived, stockInfo.isArchived());
        }

        /**
         * {@link StocksDtoMapper#mapToStockInfo(Stock)} should map {@link Stock} createdAt field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_info_should_map_created_at() {
            LocalDateTime createdAt = LocalDateTime.now();
            Stock stock = new Stock();
            stock.setCreatedAt(createdAt);

            StockInfo stockInfo = mapper.mapToStockInfo(stock);

            assertEquals(createdAt, stockInfo.createdAt());
        }

        /**
         * {@link StocksDtoMapper#mapToStockInfo(Stock)} should map {@link Stock} updatedAt field.
         * Test creates object {@link Stock} with non-null test field and try to map it to {@link StockInfo} object.
         * Then checks by equality test field before mapping and after.
         */
        @Test
        public void map_to_stock_info_should_map_updated_at() {
            LocalDateTime updatedAt = LocalDateTime.now();
            Stock stock = new Stock();
            stock.setUpdatedAt(updatedAt);

            StockInfo stockInfo = mapper.mapToStockInfo(stock);

            assertEquals(updatedAt, stockInfo.updatedAt());
        }
    }
}
