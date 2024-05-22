package ua.bookstore.online.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.bookstore.online.utils.TestDataUtils.ID_1;
import static ua.bookstore.online.utils.TestDataUtils.SHIPPING_ADDRESS;
import static ua.bookstore.online.utils.TestDataUtils.beforeEachOrderTest;
import static ua.bookstore.online.utils.TestDataUtils.getFirstOrderResponseDto;
import static ua.bookstore.online.utils.TestDataUtils.getMalvilleOrderItemResponse;
import static ua.bookstore.online.utils.TestDataUtils.getOrwellOrderItemResponse;
import static ua.bookstore.online.utils.TestDataUtils.getUser;
import static ua.bookstore.online.utils.TestDataUtils.tearDown;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import ua.bookstore.online.dto.order.OrderItemResponseDto;
import ua.bookstore.online.dto.order.OrderRequestDto;
import ua.bookstore.online.dto.order.OrderResponseDto;
import ua.bookstore.online.dto.order.StatusDto;
import ua.bookstore.online.model.Order;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIntegrationTest {
    private static final String ORDERS_URI = "/orders";
    private static final String ITEMS_URI = "/items";
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void beforeEach(@Autowired DataSource dataSource) {
        beforeEachOrderTest(dataSource);
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Create new order, expected: status - 201, response - OrderResponseDto")
    @WithMockUser
    void createOrder_CreateNewOrder_ReturnsOrderResponseDto() throws Exception {
        // Given
        LocalDateTime startOfCreating = LocalDateTime.now();
        OrderResponseDto expected = getFirstOrderResponseDto();
        String jsonRequest = objectMapper.writeValueAsString(new OrderRequestDto(SHIPPING_ADDRESS));

        // When
        MvcResult result = mockMvc.perform(post(ORDERS_URI)
                                          .content(jsonRequest)
                                          .with(user(getUser()))
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isCreated())
                                  .andReturn();

        // Then
        OrderResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        OrderResponseDto.class);
        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual,
                "id", "orderItems", "orderDate"));
        assertNotNull(actual.id());
        assertEquals(expected.orderItems().size(), actual.orderItems().size());
        assertTrue(startOfCreating.isBefore(actual.orderDate()));
    }

    @Test
    @DisplayName("Get all user's orders, expected: status - 200, response - OrderResponseDto[]")
    @WithMockUser
    void getOrderHistory_GetAllUsersOrder_ReturnsExpectedOrderDtosWithItemDtos() throws Exception {
        // Given
        List<OrderResponseDto> expected = List.of(getFirstOrderResponseDto());

        // When
        MvcResult result = mockMvc.perform(get(ORDERS_URI)
                                          .with(user(getUser()))
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk())
                                  .andReturn();

        // Then
        OrderResponseDto[] actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        OrderResponseDto[].class);
        assertNotNull(actual);
        assertEquals(expected.size(), actual.length);
        OrderResponseDto expectedOrder = expected.getFirst();
        OrderResponseDto actualOrder = actual[0];
        assertTrue(EqualsBuilder.reflectionEquals(expectedOrder, actualOrder, "orderItems"));
        assertTrue(actualOrder.orderItems().containsAll(expectedOrder.orderItems()));
    }

    @Test
    @DisplayName("Successfully update status of order, expected: status - 202")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void updateStatus_UpdatingStatusOfOrder_RespondAccepted() throws Exception {
        // Given
        String url = ORDERS_URI + "/" + ID_1;
        StatusDto expected = new StatusDto(Order.Status.PROCESSED);
        String jsonRequest = objectMapper.writeValueAsString(expected);

        // When / Then
        mockMvc.perform(patch(url)
                       .content(jsonRequest)
                       .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andReturn();
    }

    @Test
    @DisplayName("Get order items from user's order, expected: status - 200, response - OrderItemResponseDto[]")
    @WithMockUser
    void getAllOrderItems_GetAllUsersOrderItems_ReturnsExpectedOrderItemDtos() throws Exception {
        // Given
        String url = ORDERS_URI + '/' + ID_1 + ITEMS_URI;
        List<OrderItemResponseDto> expected =
                List.of(getOrwellOrderItemResponse(), getMalvilleOrderItemResponse());

        // When
        MvcResult result = mockMvc.perform(get(url)
                                          .with(user(getUser()))
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk())
                                  .andReturn();

        // Then
        OrderItemResponseDto[] actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        OrderItemResponseDto[].class);
        assertNotNull(actual);
        assertEquals(expected.size(), actual.length);
        assertEquals(expected.getFirst(), actual[0]);
        assertEquals(expected.getLast(), actual[1]);
    }

    @Test
    @DisplayName("Get user's order item by ID, expected: status - 200, response - OrderItemResponseDto")
    @WithMockUser
    void getOrderItem_GetOrderItemById_ReturnsExpectedOrderItemDto() throws Exception {
        // Given
        String url = ORDERS_URI + '/' + ID_1 + ITEMS_URI + '/' + ID_1;
        OrderItemResponseDto expected = getOrwellOrderItemResponse();

        // When
        MvcResult result = mockMvc.perform(get(url)
                                          .with(user(getUser()))
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk())
                                  .andReturn();

        // Then
        OrderItemResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        OrderItemResponseDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
