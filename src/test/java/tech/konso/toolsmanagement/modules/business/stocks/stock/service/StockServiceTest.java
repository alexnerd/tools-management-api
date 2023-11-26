package tech.konso.toolsmanagement.modules.business.stocks.stock.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import tech.konso.toolsmanagement.PostgreSQLContainerExtension;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockFilterInfo;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockFilterResponse;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockInfo;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockRequest;
import tech.konso.toolsmanagement.modules.business.stocks.stock.persistence.dao.Stock;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static tech.konso.toolsmanagement.modules.business.stocks.stock.persistence.specification.StockSpecification.*;
import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

/**
 * Stock service layer tests.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */
@SpringBootTest
@ExtendWith(PostgreSQLContainerExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StockServiceTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StockService service;


    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e9801', 'name_1', 'address_1')");
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e9802', 'name_2', 'address_2')");
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e9803', 'name_3', 'address_3')");
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e9804', 'name_4', 'address_4')");
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e9805', 'name_5', 'address_5')");
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e9806', 'name_6', 'address_6')");
        jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address, is_archived) VALUES ('935921a7-692e-4ee4-a089-2695b68e9807', 'name_7', 'address_7', 'true')");
    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM stocks_stock");

    }

    /**
     * Create {@link StockRequest.StockRequestBuilder} object with required non-null fields.
     */
    private StockRequest.StockRequestBuilder getDefaultStockRequest() {
        return StockRequest.builder()
                .name("Name")
                .address("address")
                .isArchived(false);
    }


    @Nested
    class SaveTests {
        /**
         * {@link StockService#save(StockRequest)}} should save {@link Stock} object.
         * Test creates dto object {@link StockRequest} and then using {@link StockService#save(StockRequest)}
         * try to save new {@link Stock} object to database.
         * Then checks returns {@link Stock} object with null fields.
         */
        @Test
        public void save_should_save_stock_test() {
            StockRequest rq = getDefaultStockRequest()
                    .build();

            Stock savedStock = service.save(rq);

            assertNotNull(savedStock.getId());
            assertNotNull(savedStock.getUuid());
            assertNotNull(savedStock.getName());
            assertNotNull(savedStock.getAddress());
            assertNotNull(savedStock.getCreatedAt());
            assertNotNull(savedStock.getUpdatedAt());
        }

        /**
         * {@link StockService#save(StockRequest)}} should save {@link Stock} object with company uuid.
         * Test creates dto object {@link StockRequest} and then using {@link StockService#save(StockRequest)}
         * try to save new {@link Stock} object to database.
         * Then checks returns {@link Stock} object if id not null and company uuid field equals this field
         * from dto object {@link StockRequest}.
         */
        @Test
        public void save_should_save_stock_company_uuid_test() {
            UUID companyUuid = UUID.fromString("935921a7-692e-4ee4-a089-2695b68e9801");
            StockRequest rq = getDefaultStockRequest()
                    .companyUuid(companyUuid)
                    .build();

            Stock savedStock = service.save(rq);

            assertEquals(rq.companyUuid(), savedStock.getCompanyUuid());
        }

        /**
         * {@link StockService#save(StockRequest)}} should save {@link Stock} object with name.
         * Test creates dto object {@link StockRequest} and then using {@link StockService#save(StockRequest)}
         * try to save new {@link Stock} object to database.
         * Then checks returns {@link Stock} object if name field equals this field
         * from dto object {@link StockRequest}.
         */
        @Test
        public void save_should_save_stock_name_test() {
            String name = "Kevin";
            StockRequest rq = getDefaultStockRequest()
                    .name(name)
                    .build();

            Stock savedStock = service.save(rq);

            assertEquals(rq.name(), savedStock.getName());
        }

        /**
         * {@link StockService#save(StockRequest)}} should save {@link Stock} object with address.
         * Test creates dto object {@link StockRequest} and then using {@link StockService#save(StockRequest)}
         * try to save new {@link Stock} object to database.
         * Then checks returns {@link Stock} object if address field equals this field
         * from dto object {@link StockRequest}.
         */
        @Test
        public void save_should_save_stock_address_test() {
            String address = "address";
            StockRequest rq = getDefaultStockRequest()
                    .address(address)
                    .build();

            Stock savedStock = service.save(rq);

            assertEquals(rq.address(), savedStock.getAddress());
        }

        /**
         * {@link StockService#save(StockRequest)}} should save {@link Stock} object with is archived.
         * Test creates dto object {@link StockRequest} and then using {@link StockService#save(StockRequest)}
         * try to save new {@link Stock} object to database.
         * Then checks returns {@link Stock} object if is archived field equals this field
         * from dto object {@link StockRequest}.
         */
        @Test
        public void save_should_save_stock_is_archived_test() {
            Boolean isArchived = Boolean.TRUE;
            StockRequest rq = getDefaultStockRequest()
                    .isArchived(isArchived)
                    .build();

            Stock savedStock = service.save(rq);

            assertEquals(rq.isArchived(), savedStock.getIsArchived());
        }
    }

    @Nested
    class FindByIdTests {
        /**
         * {@link StockService#findById(Long)} should return {@link StockInfo} by id from database.
         * Test checks equality stock id (received from jdbcTemplate request)
         * with id of stock object received from {@link StockService#findById(Long)}
         */
        @Test
        public void findById_should_return_stock_test() {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE uuid = '935921a7-692e-4ee4-a089-2695b68e9801' AND is_archived IS FALSE", Long.class);

            StockInfo stockInfo = service.findById(stockId);

            assertEquals(stockId, stockInfo.id());
        }

        /**
         * {@link StockService#findById(Long)} should throw {@link BPException} exception
         * if {@link Stock} with id not exist in database.
         * Test try to find stock whit id = -1 (negative num,ber guaranties, that no such id exists in database)
         * and check if {@link BPException} is thrown.
         */
        @Test
        public void findById_should_throw_exception_on_not_found_stock_test() {
            long stockId = -1;

            assertThrows(BPException.class, () -> service.findById(stockId));
        }
    }

    @Nested
    class FindAllTest {
        /**
         * {@link StockService#findAll(int, int, Specification)} should return all not archived {@link Stock} objects.
         * Test counts all not archived stock objects in the database using jdbcTemplate
         * Then test build isArchived specification for not archived stocks and try to find them with {@link StockService#findAll(int, int, Specification)}
         * Then test checks if counts not archived stocks from jdbcTemplate equals totalItems from {@link StockFilterResponse}
         * received from {@link StockService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_should_find_all_not_archived_stocks_test() {
            long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM stocks_stock WHERE is_archived IS FALSE", Long.class);
            Specification<Stock> spec = specBuilder(isArchivedSpec(false)).build();

            Page<StockFilterInfo> foundedStocks = service.findAll(0, 100, spec);

            assertEquals(countNotArchived, foundedStocks.getTotalElements());
        }

        /**
         * {@link StockService#findAll(int, int, Specification)} should return all archived {@link Stock} objects.
         * Test counts all archived stock objects in the database using jdbcTemplate
         * Then test build isArchived specification for archived stocks and try to find them with {@link StockService#findAll(int, int, Specification)}
         * Then test checks if counts archived stocks from jdbcTemplate equals totalItems from {@link StockFilterResponse}
         * received from {@link StockService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_should_find_all_archived_stocks_test() {
            long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM stocks_stock WHERE is_archived IS TRUE", Long.class);
            Specification<Stock> spec = specBuilder(isArchivedSpec(true)).build();

            Page<StockFilterInfo> foundedStocks = service.findAll(0, 100, spec);

            assertEquals(countNotArchived, foundedStocks.getTotalElements());
        }

        /**
         * {@link StockService#findAll(int, int, Specification)} should return {@link Stock} objects by like name pattern.
         * Test counts all stock objects in the database matches %ran% pattern using jdbcTemplate
         * Then test build like specification for name with %ran% pattern and try to find them with {@link StockService#findAll(int, int, Specification)}
         * Then test checks if counts stocks from jdbcTemplate equals totalItems from {@link StockFilterResponse}
         * received from {@link StockService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_should_find_by_name_stocks_test() {
            String likeName = "sto";
            jdbcTemplate.update("INSERT INTO stocks_stock (name, address, uuid) VALUES ('STOCK_ONE', 'ADDRESS_1', '935921a7-692e-4ee4-a089-2695b68e9907')");
            jdbcTemplate.update("INSERT INTO stocks_stock (name, address, uuid) VALUES ('STOCK_TWO', 'ADDRESS_2','935921a7-692e-4ee4-a089-2695b68e9908')");
            long countNotArchived = jdbcTemplate.queryForObject("SELECT count(*) FROM stocks_stock WHERE LOWER (name) LIKE '%" + likeName + "%'", Long.class);
            Specification<Stock> spec = specBuilder(likeSpec(likeName)).build();

            Page<StockFilterInfo> foundedStocks = service.findAll(0, 100, spec);

            assertEquals(countNotArchived, foundedStocks.getTotalElements());
        }

        /**
         * {@link StockService#findAll(int, int, Specification)} should sort {@link Stock} objects by name in asc order.
         * Test receives all stock names from the database in asc order
         * Then test build sort specification for order stocks by name in asc order and try to find them with {@link StockService#findAll(int, int, Specification)}
         * Then test checks if order of stock names received from jdbcTemplate equals order of stock names from {@link StockFilterResponse}
         * received from {@link StockService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_should_sort_asc_by_name_stocks_test() {
            List<String> stockNames = jdbcTemplate.queryForList("SELECT name FROM stocks_stock ORDER BY name ASC", String.class);
            Specification<Stock> spec = specBuilder(sortSpec("name,asc")).build();

            Page<StockFilterInfo> foundedStocks = service.findAll(0, 100, spec);

            List<String> resultStockNames = foundedStocks.getContent().stream().map(StockFilterInfo::name).toList();
            assertIterableEquals(stockNames, resultStockNames);
        }

        /**
         * {@link StockService#findAll(int, int, Specification)} should sort {@link Stock} objects by name in desc order.
         * Test receives all stock names from the database in desc order
         * Then test build sort specification for order stocks by name in desc order and try to find them with {@link StockService#findAll(int, int, Specification)}
         * Then test checks if order of stock names received from jdbcTemplate equals order of stock names from {@link StockFilterResponse}
         * received from {@link StockService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_should_sort_desc_by_name_stocks_test() {
            List<String> stockNames = jdbcTemplate.queryForList("SELECT name FROM stocks_stock ORDER BY name DESC", String.class);
            Specification<Stock> spec = specBuilder(sortSpec("name,desc")).build();

            Page<StockFilterInfo> foundedStocks = service.findAll(0, 100, spec);

            List<String> resultStockNames = foundedStocks.getContent().stream().map(StockFilterInfo::name).toList();
            assertIterableEquals(stockNames, resultStockNames);
        }

        /**
         * {@link StockService#findAll(int, int, Specification)} should sort {@link Stock} objects by createdAt in asc order.
         * Test receives all stock names from the database ordered by createdAt in asc order
         * Then test build sort specification for order stocks by createdAt in asc order and try to find them with {@link StockService#findAll(int, int, Specification)}
         * Then test checks if order of stock names received from jdbcTemplate equals order of stock names from {@link StockFilterResponse}
         * received from {@link StockService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_should_sort_asc_by_created_date_stocks_test() {
            List<String> stockNames = jdbcTemplate.queryForList("SELECT name FROM stocks_stock ORDER BY created_at ASC", String.class);
            Specification<Stock> spec = specBuilder(sortSpec("createdat,asc")).build();

            Page<StockFilterInfo> foundedStocks = service.findAll(0, 100, spec);

            List<String> resultStockNames = foundedStocks.getContent().stream().map(StockFilterInfo::name).toList();
            assertIterableEquals(stockNames, resultStockNames);
        }

        /**
         * {@link StockService#findAll(int, int, Specification)} should sort {@link Stock} objects by createdAt in desc order.
         * Test receives all stock names from the database ordered by createdAt in desc order
         * Then test build sort specification for order stocks by createdAt in desc order and try to find them with {@link StockService#findAll(int, int, Specification)}
         * Then test checks if order of stock names received from jdbcTemplate equals order of stock names from {@link StockFilterResponse}
         * received from {@link StockService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_should_sort_desc_by_created_date_stocks_test() {
            List<String> stockNames = jdbcTemplate.queryForList("SELECT name FROM stocks_stock ORDER BY created_at DESC", String.class);
            Specification<Stock> spec = specBuilder(sortSpec("createdat,desc")).build();

            Page<StockFilterInfo> foundedStocks = service.findAll(0, 100, spec);

            List<String> resultStockNames = foundedStocks.getContent().stream().map(StockFilterInfo::name).toList();
            assertIterableEquals(stockNames, resultStockNames);
        }

        /**
         * {@link StockService#findAll(int, int, Specification)} without filters, by default should sort {@link Stock} objects by createdAt in desc order.
         * Test receives all stock names from the database ordered by createdAt in desc order
         * Then test build sort specification null parameter and try to find stocks with {@link StockService#findAll(int, int, Specification)}
         * Then test checks if order of stock names received from jdbcTemplate equals order of stock names from {@link StockFilterResponse}
         * received from {@link StockService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_with_null_filter_should_sort_by_default_desc_by_created_date_stocks_test() {
            List<String> stockNames = jdbcTemplate.queryForList("SELECT name FROM stocks_stock ORDER BY created_at DESC", String.class);
            Specification<Stock> spec = specBuilder(sortSpec(null)).build();

            Page<StockFilterInfo> foundedStocks = service.findAll(0, 100, spec);

            List<String> resultStockNames = foundedStocks.getContent().stream().map(StockFilterInfo::name).toList();
            assertIterableEquals(stockNames, resultStockNames);
        }

        /**
         * {@link StockService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Stock} objects by createdAt in desc order.
         * Test receives all stock names from the database ordered by createdAt in desc order
         * Then test build sort specification with empty field "  " parameter and try to find stocks with {@link StockService#findAll(int, int, Specification)}
         * Then test checks if order of stock names received from jdbcTemplate equals order of stock names from {@link StockFilterResponse}
         * received from {@link StockService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_with_blank_filter_should_sort_by_default_desc_by_created_date_stocks_test() {
            List<String> stockNames = jdbcTemplate.queryForList("SELECT name FROM stocks_stock ORDER BY created_at DESC", String.class);
            Specification<Stock> spec = specBuilder(sortSpec(" ")).build();

            Page<StockFilterInfo> foundedStocks = service.findAll(0, 100, spec);

            List<String> resultStockNames = foundedStocks.getContent().stream().map(StockFilterInfo::name).toList();
            assertIterableEquals(stockNames, resultStockNames);
        }

        /**
         * {@link StockService#findAll(int, int, Specification)} with unsupported filter, by default should sort {@link Stock} objects by createdAt in desc order.
         * Test receives all stock names from the database ordered by createdAt in desc order
         * Then test build sort specification with unsupported parameter and try to find stocks with {@link StockService#findAll(int, int, Specification)}
         * Then test checks if order of stock names received from jdbcTemplate equals order of stock names from {@link StockFilterResponse}
         * received from {@link StockService#findAll(int, int, Specification)}
         */
        @Test
        public void findAll_with_unsupported_filter_should_sort_by_default_desc_by_created_date_stocks_test() {
            List<String> stockNames = jdbcTemplate.queryForList("SELECT name FROM stocks_stock ORDER BY created_at DESC", String.class);
            Specification<Stock> spec = specBuilder(sortSpec("unsupported_filter")).build();

            Page<StockFilterInfo> foundedStocks = service.findAll(0, 100, spec);

            List<String> resultStockNames = foundedStocks.getContent().stream().map(StockFilterInfo::name).toList();
            assertIterableEquals(stockNames, resultStockNames);
        }

        /**
         * {@link StockService#findAll(int, int, Specification)} should return stocks with specified size.
         * Test creates null specification.
         * Then test with page size 1 returns {@link StockFilterResponse} from  {@link StockService#findAll(int, int, Specification)}
         * Then test checks total received items and concrete size of founded stocks in concrete page.
         */
        @Test
        public void findAll_with_page_size_one_should_return_one_stock_test() {
            Specification<Stock> spec = specBuilder(sortSpec(null)).build();

            Page<StockFilterInfo> foundedStocks = service.findAll(0, 1, spec);

            assertEquals(7, foundedStocks.getTotalElements());
            assertEquals(1, foundedStocks.getContent().size());
        }
    }
}
