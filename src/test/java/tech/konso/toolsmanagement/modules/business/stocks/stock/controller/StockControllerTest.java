package tech.konso.toolsmanagement.modules.business.stocks.stock.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import tech.konso.toolsmanagement.modules.business.persons.commons.AbstractControllerTest;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockFilterInfo;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockFilterResponse;
import tech.konso.toolsmanagement.modules.business.stocks.stock.controller.dto.StockRequest;
import tech.konso.toolsmanagement.modules.business.stocks.stock.persistence.dao.Stock;
import tech.konso.toolsmanagement.modules.business.stocks.stock.service.StockService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Stock controller layer tests.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */
public class StockControllerTest extends AbstractControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    private String urlEndpoint() {
        return url + "/v1/stocks/stock";
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
    class FindTests {
        /**
         * {@link StockController#find(Long)} should return {@link Stock} by id from database.
         * Test checks status code 200 and equality stockId (received from jdbcTemplate request)
         * with id of stock object received from {@link StockService#findById(Long)} and name.
         */
        @Test
        public void find_should_return_stock_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);

            mockMvc.perform(get(urlEndpoint() + "/" + stockId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            Stock.class,
                            dto -> dto.getId() == stockId && dto.getName().equals("name_1")
                    )));
        }

        /**
         * {@link StockController#find(Long)} should return not found if {@link Stock} with id not exist in database.
         * Test try to find stock whit id = -1 (negative number guaranties, that no such id exists in database)
         * and check if controller return not found with detailed error message in header.
         */
        @Test
        public void find_should_return_not_found_test() throws Exception {
            long stockId = -1L;

            mockMvc.perform(get(urlEndpoint() + "/" + stockId))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(header().stringValues("detail", "Stock not found id: " + stockId));
        }
    }

    @Nested
    class FindAllTests {
        /**
         * {@link StockController#findAll(int, int, String, Boolean, String)}
         * should return bad request with incorrect page number. Test try to search all stocks whit page = 0
         * and check if controller return bad request with detailed error message in header.
         */
        @Test
        public void findAll_with_incorrect_page_should_return_bab_request_test() throws Exception {
            String tail = "?page=0&size=20";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(header().stringValues("detail", "findAll.page: must be greater than or equal to 1"));
        }

        /**
         * {@link StockController#findAll(int, int, String, Boolean, String)}
         * should return bad request with incorrect size number. Test try to search all stocks whit size = 99999
         * and check if controller return bad request with detailed error message in header.
         */
        @Test
        public void findAll_with_incorrect_size_should_return_bab_request_test() throws Exception {
            String tail = "?page=1&size=99999";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isBadRequest()).
                    andExpect(header().stringValues("detail", "findAll.size: must be less than or equal to 50"));
        }

        /**
         * {@link StockController#findAll(int, int, String, Boolean, String)}  without filters should return all not archived stocks.
         * Test counts all not archived stocks from the database.
         * Then test make request to find all stocks and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_return_stocks_without_filters_test() throws Exception {
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM stocks_stock WHERE is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            StockFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link StockController#findAll(int, int, String, Boolean, String)}  with archived filter,
         * should return all archived stocks.
         * Test counts all archived stocks from the database.
         * Then test make request to find all stocks and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_filter_archived_stocks_test() throws Exception {
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM stocks_stock WHERE is_archived IS TRUE", Long.class);
            String tail = "?page=1&size=20&isArchived=true";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            StockFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link StockController#findAll(int, int, String, Boolean, String)}  with not archived filter,
         * should return all not archived stocks.
         * Test counts all not archived stocks from the database.
         * Then test make request to find all stocks and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_filter_not_archived_stocks_test() throws Exception {
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM stocks_stock WHERE is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20&isArchived=false";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            StockFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link StockController#findAll(int, int, String, Boolean, String)}  with filter by full name,
         * should return stock with this name.
         * Test counts all stock with concrete name.
         * Then test make request to find all stocks and checks if it returns the same number, as plane jdbc request,
         * and checks if returns name equals with predefined name.
         */
        @Test
        public void findAll_should_filter_full_name_stocks_test() throws Exception {
            String stockName = "name_1";
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM stocks_stock WHERE name = '" + stockName + "' AND is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20&name=" + stockName;

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            StockFilterResponse.class,
                            dto -> dto.totalItems() == count && dto.stocks().get(0).name().equals(stockName))
                    ));
        }

        /**
         * {@link StockController#findAll(int, int, String, Boolean, String)}  with filter by part of name,
         * should return stock with this name.
         * Test counts all stock with like filter by name.
         * Then test make request to find all stocks and checks if it returns the same number, as plane jdbc request.
         */
        @Test
        public void findAll_should_filter_like_name_stocks_test() throws Exception {
            jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e1801', 'FOOD_MAIN', 'address_1')");
            jdbcTemplate.update("INSERT INTO stocks_stock (uuid, name, address) VALUES ('935921a7-692e-4ee4-a089-2695b68e1802', 'FOOD_SUPPORT', 'address_2')");
            String stockName = "food";
            long count = jdbcTemplate.queryForObject("SELECT count(*) FROM stocks_stock WHERE LOWER (name) LIKE '%" + stockName + "%' AND is_archived IS FALSE", Long.class);
            String tail = "?page=1&size=20&name=" + stockName;

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            StockFilterResponse.class,
                            dto -> dto.totalItems() == count)
                    ));
        }

        /**
         * {@link StockController#findAll(int, int, String, Boolean, String)}  with filter sorting by name asc,
         * should return sorted stocks by names asc.
         * Test returns stock names from database(using jdbcTemplate) ordered by name asc.
         * Then test make request to find all stocks and checks if it returns the stocks in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_name_asc_stocks_test() throws Exception {
            List<String> stockNames = jdbcTemplate.queryForList("SELECT name FROM stocks_stock WHERE is_archived IS FALSE ORDER BY name ASC", String.class);
            String tail = "?page=1&size=20&sort=name,asc";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            StockFilterResponse.class,
                            dto -> {
                                List<String> stockNamesResponse = dto.stocks().stream().map(StockFilterInfo::name).toList();
                                assertIterableEquals(stockNames, stockNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link StockController#findAll(int, int, String, Boolean, String)}  with filter sorting by name desc,
         * should return sorted stocks by name desc.
         * Test returns stock names from database(using jdbcTemplate) ordered by name desc.
         * Then test make request to find all stocks and checks if it returns the stocks in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_name_desc_stocks_test() throws Exception {
            List<String> stockNames = jdbcTemplate.queryForList("SELECT name FROM stocks_stock WHERE is_archived IS FALSE ORDER BY name DESC", String.class);
            String tail = "?page=1&size=20&sort=name,desc";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            StockFilterResponse.class,
                            dto -> {
                                List<String> stockNamesResponse = dto.stocks().stream().map(StockFilterInfo::name).toList();
                                assertIterableEquals(stockNames, stockNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link StockController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date asc,
         * should return sorted stocks by create date asc.
         * Test returns stock names from database(using jdbcTemplate) ordered by created date asc.
         * Then test make request to find all stocks and checks if it returns the stocks in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_create_date_asc_stocks_test() throws Exception {
            List<String> stockNames = jdbcTemplate.queryForList("SELECT name FROM stocks_stock WHERE is_archived IS FALSE ORDER BY created_at ASC", String.class);
            String tail = "?page=1&size=20&sort=createdAt,asc";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            StockFilterResponse.class,
                            dto -> {
                                List<String> stockNamesResponse = dto.stocks().stream().map(StockFilterInfo::name).toList();
                                assertIterableEquals(stockNames, stockNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link StockController#findAll(int, int, String, Boolean, String)}  with filter sorting by create date desc,
         * should return sorted stocks by create date desc.
         * Test returns stock names from database(using jdbcTemplate) ordered by created date desc.
         * Then test make request to find all stocks and checks if it returns the stocks in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_create_date_desc_stocks_test() throws Exception {
            List<String> stockNames = jdbcTemplate.queryForList("SELECT name FROM stocks_stock WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);

            mockMvc.perform(get(urlEndpoint() + "?page=1&size=20&sort=createdAt,desc"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            StockFilterResponse.class,
                            dto -> {
                                List<String> stockNamesResponse = dto.stocks().stream().map(StockFilterInfo::name).toList();
                                assertIterableEquals(stockNames, stockNamesResponse);
                                return true;
                            })
                    ));
        }

        /**
         * {@link StockController#findAll(int, int, String, Boolean, String)}  without filter by default
         * should return sorted stocks by create date desc.
         * Test returns stock names from database(using jdbcTemplate) ordered by created date desc.
         * Then test make request to find all stocks and checks if it returns the stocks in the same order, as plane jdbc request.
         */
        @Test
        public void findAll_should_sort_by_create_date_desc_stocks_by_default_test() throws Exception {
            List<String> stockNames = jdbcTemplate.queryForList("SELECT name FROM stocks_stock WHERE is_archived IS FALSE ORDER BY created_at DESC", String.class);
            String tail = "?page=1&size=20";

            mockMvc.perform(get(urlEndpoint() + tail))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(dtoMatcher(
                            StockFilterResponse.class,
                            dto -> {
                                List<String> stockNamesResponse = dto.stocks().stream().map(StockFilterInfo::name).toList();
                                assertIterableEquals(stockNames, stockNamesResponse);
                                return true;
                            })
                    ));
        }
    }

    @Nested
    class UpdateTests {
        /**
         * {@link StockController#update(StockRequest)}  should update {@link Stock} isArchived flag.
         * Test finds existing stock id in database with jdbcTemplate.
         * Then send request for update isArchived flag by id.
         * Then checks if isArchived was updated or not (by compare {@link StockRequest} isArchived flag and flag received from database).
         */
        @Test
        public void update_should_update_stock_is_archived_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            StockRequest rq = getDefaultStockRequest()
                    .id(stockId)
                    .isArchived(true)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            Boolean isArchived = jdbcTemplate.queryForObject("SELECT is_archived FROM stocks_stock WHERE stock_id = " + stockId, Boolean.class);
            assertEquals(rq.isArchived(), isArchived);
        }

        /**
         * {@link StockController#update(StockRequest)} should return bad request with null stock isArchived.
         * Test finds existing stock id in database with jdbcTemplate.
         * Then send request for update by id with null stock isArchived.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_null_is_archived_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            StockRequest rq = getDefaultStockRequest()
                    .id(stockId)
                    .isArchived(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link StockController#update(StockRequest)} should update {@link Stock} name field.
         * Test finds existing stock id in database with jdbcTemplate.
         * Then send request for update stock name by id.
         * Then checks if name was updated or not (by compare {@link StockRequest} name and stockName received from database).
         */
        @Test
        public void update_should_update_stock_name_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            StockRequest rq = getDefaultStockRequest()
                    .id(stockId)
                    .name("Alex")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            String newStockName = jdbcTemplate.queryForObject("SELECT name FROM stocks_stock WHERE stock_id = " + stockId + " AND is_archived IS FALSE", String.class);
            assertEquals(rq.name(), newStockName);
        }

        /**
         * {@link StockController#update(StockRequest)} should return bad request with null stock name.
         * Test finds existing stock id in database with jdbcTemplate.
         * Then send request for update by id with null stock name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_null_name_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            StockRequest rq = getDefaultStockRequest()
                    .id(stockId)
                    .name(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link StockController#update(StockRequest)} should return bad request with blank stock name.
         * Test finds existing stock id in database with jdbcTemplate.
         * Then send request for update by id with blank stock name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_blank_name_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            StockRequest rq = getDefaultStockRequest()
                    .id(stockId)
                    .name("  ")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link StockController#update(StockRequest)} should return bad request with empty stock name.
         * Test finds existing stock id in database with jdbcTemplate.
         * Then send request for update by id with empty stock name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_empty_name_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            StockRequest rq = getDefaultStockRequest()
                    .id(stockId)
                    .name("")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link StockController#update(StockRequest)} should update {@link Stock} address field.
         * Test finds existing stock id in database with jdbcTemplate.
         * Then send request for update stock address by id.
         * Then checks if address was updated or not (by compare {@link StockRequest} address and stock address received from database).
         */
        @Test
        public void update_should_update_stock_address_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE address = 'address_1' AND is_archived IS FALSE", Long.class);
            StockRequest rq = getDefaultStockRequest()
                    .id(stockId)
                    .address("NEW_ADDRESS")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            String newStockAddress = jdbcTemplate.queryForObject("SELECT address FROM stocks_stock WHERE stock_id = " + stockId + " AND is_archived IS FALSE", String.class);
            assertEquals(rq.address(), newStockAddress);
        }

        /**
         * {@link StockController#update(StockRequest)} should return bad request with null stock address.
         * Test finds existing stock id in database with jdbcTemplate.
         * Then send request for update by id with null stock address.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_null_address_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE address = 'address_1' AND is_archived IS FALSE", Long.class);
            StockRequest rq = getDefaultStockRequest()
                    .id(stockId)
                    .address(null)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link StockController#update(StockRequest)} should return bad request with blank stock address.
         * Test finds existing stock id in database with jdbcTemplate.
         * Then send request for update by id with blank stock address.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_blank_address_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE address = 'address_1' AND is_archived IS FALSE", Long.class);
            StockRequest rq = getDefaultStockRequest()
                    .id(stockId)
                    .address("  ")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link StockController#update(StockRequest)} should return bad request with empty stock address.
         * Test finds existing stock id in database with jdbcTemplate.
         * Then send request for update by id with empty stock address.
         * Then checks if controller response with bad request.
         */
        @Test
        public void update_should_return_bad_request_for_empty_address_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE address = 'address_1' AND is_archived IS FALSE", Long.class);
            StockRequest rq = getDefaultStockRequest()
                    .id(stockId)
                    .address("")
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link StockController#update(StockRequest)} should update {@link Stock} companyUuid field.
         * Test finds existing stock id in database with jdbcTemplate.
         * Then send request for update stock companyUuid by id.
         * Then checks if phone number was updated or not (by compare {@link StockRequest} companyUuid and companyUuid received from database).
         */
        @Test
        public void update_should_update_stock_company_uuid_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            UUID uuid = UUID.fromString("735921a7-4444-4ee4-a089-2695b68e9101");
            StockRequest rq = getDefaultStockRequest()
                    .id(stockId)
                    .companyUuid(uuid)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            UUID companyUuid = jdbcTemplate.queryForObject("SELECT company_uuid FROM stocks_stock WHERE stock_id = " + stockId + " AND is_archived IS FALSE", UUID.class);
            assertEquals(rq.companyUuid(), companyUuid);
        }

        /**
         * {@link StockController#update(StockRequest)} should update {@link Stock} responsibleCompanyUuid field.
         * Test finds existing stock id in database with jdbcTemplate.
         * Then send request for update stock responsibleCompanyUuid by id.
         * Then checks if phone number was updated or not (by compare {@link StockRequest} responsibleCompanyUuid and responsibleCompanyUuid received from database).
         */
        @Test
        public void update_should_update_stock_responsible_company_uuid_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            UUID uuid = UUID.fromString("735921a7-4444-4ee4-a089-2695b68e9101");
            StockRequest rq = getDefaultStockRequest()
                    .id(stockId)
                    .responsibleCompanyUuid(uuid)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            UUID responsibleCompanyUuid = jdbcTemplate.queryForObject("SELECT responsible_company_uuid FROM stocks_stock WHERE stock_id = " + stockId + " AND is_archived IS FALSE", UUID.class);
            assertEquals(rq.responsibleCompanyUuid(), responsibleCompanyUuid);
        }

        /**
         * {@link StockController#update(StockRequest)} should update {@link Stock} responsiblePersonUuid field.
         * Test finds existing stock id in database with jdbcTemplate.
         * Then send request for update stock responsiblePersonUuid by id.
         * Then checks if phone number was updated or not (by compare {@link StockRequest} responsiblePersonUuid and responsiblePersonUuid received from database).
         */
        @Test
        public void update_should_update_stock_responsible_person_uuid_test() throws Exception {
            long stockId = jdbcTemplate.queryForObject("SELECT stock_id FROM stocks_stock WHERE name = 'name_1' AND is_archived IS FALSE", Long.class);
            UUID uuid = UUID.fromString("735921a7-4444-4ee4-a089-2695b68e9101");
            StockRequest rq = getDefaultStockRequest()
                    .id(stockId)
                    .responsiblePersonUuid(uuid)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNoContent());

            UUID responsiblePersonUuid = jdbcTemplate.queryForObject("SELECT responsible_person_uuid FROM stocks_stock WHERE stock_id = " + stockId + " AND is_archived IS FALSE", UUID.class);
            assertEquals(rq.responsiblePersonUuid(), responsiblePersonUuid);
        }

        /**
         * {@link StockController#update(StockRequest)} should return not found if stock with searching id not exist in database.
         * Test send request for update by not existing id.
         * Then checks if controller response with not found.
         */
        @Test
        public void update_should_return_not_found_for_not_existing_id_test() throws Exception {
            StockRequest rq = getDefaultStockRequest()
                    .id(-1L)
                    .build();

            mockMvc.perform(put(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isNotFound());
        }


    }

    @Nested
    class SaveTests {
        /**
         * {@link StockController#save(StockRequest)} should save {@link Stock} object.
         * Test checks if with given name not exists in database.
         * Then sends request to create new stock and checks status equals created.
         * Then receive number of stocks exists in database with new name.
         * Then checks if number of stocks with new name equals one (new stock saved to database).
         */
        @Test
        public void save_should_save_new_stock_test() throws Exception {
            String stockName = "FOOD_STOCK";
            Long countStocks = jdbcTemplate.queryForObject("SELECT count(*) FROM stocks_stock WHERE name = '" + stockName + "' AND is_archived IS FALSE", Long.class);
            assertEquals(0, countStocks);
            StockRequest rq = getDefaultStockRequest()
                    .name(stockName)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isCreated());

            Long countSavedStocks = jdbcTemplate.queryForObject("SELECT count(*) FROM stocks_stock WHERE name = '" + stockName + "' AND is_archived IS FALSE", Long.class);
            assertEquals(1L, countSavedStocks);
        }

        /**
         * {@link StockController#save(StockRequest)} should not save {@link Stock} object if stock name is null.
         * Test sends request to create new stock with null stock name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_name_null_test() throws Exception {
            StockRequest rq = getDefaultStockRequest()
                    .name(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link StockController#save(StockRequest)} should not save {@link Stock} object if stock name is empty.
         * Test sends request to create new stock with empty stock name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_name_empty_test() throws Exception {
            StockRequest rq = getDefaultStockRequest()
                    .name("")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link StockController#save(StockRequest)} should not save {@link Stock} object if stock name is blank.
         * Test sends request to create new stock with blank stock name.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_name_blank_test() throws Exception {
            StockRequest rq = getDefaultStockRequest()
                    .name("  ")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link StockController#save(StockRequest)} should not save {@link Stock} object if stock address is null.
         * Test sends request to create new stock with null stock address.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_address_null_test() throws Exception {
            StockRequest rq = getDefaultStockRequest()
                    .address(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link StockController#save(StockRequest)} should not save {@link Stock} object if stock address is empty.
         * Test sends request to create new stock with empty stock address.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_address_empty_test() throws Exception {
            StockRequest rq = getDefaultStockRequest()
                    .address("")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }

        /**
         * {@link StockController#save(StockRequest)} should not save {@link Stock} object if stock address is blank.
         * Test sends request to create new stock with blank stock address.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_address_blank_test() throws Exception {
            StockRequest rq = getDefaultStockRequest()
                    .address("  ")
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }


        /**
         * {@link StockController#save(StockRequest)} should not save {@link Stock} object if archived flag is null.
         * Test sends request to create new stock with null archived flag.
         * Then checks if controller response with bad request.
         */
        @Test
        public void save_should_not_save_if_is_archived_null_test() throws Exception {
            StockRequest rq = getDefaultStockRequest()
                    .isArchived(null)
                    .build();

            mockMvc.perform(post(urlEndpoint())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(rq)))
                    .andExpect(status().isBadRequest());
        }
    }

}
