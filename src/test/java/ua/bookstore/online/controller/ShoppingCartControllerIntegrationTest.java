package ua.bookstore.online.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_1;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_2;
import static ua.bookstore.online.utils.ConstantAndMethod.TITLE_MOBI_DICK;
import static ua.bookstore.online.utils.ConstantAndMethod.beforeEachShoppingCartTest;
import static ua.bookstore.online.utils.ConstantAndMethod.getCartItemRequestDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getCartItemResponseDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getQuantityDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getUser;
import static ua.bookstore.online.utils.ConstantAndMethod.tearDown;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ua.bookstore.online.dto.shopping.cart.CartItemRequestDto;
import ua.bookstore.online.dto.shopping.cart.CartItemResponseDto;
import ua.bookstore.online.dto.shopping.cart.QuantityDto;
import ua.bookstore.online.dto.shopping.cart.ShoppingCartDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerIntegrationTest {
    private static final String CART_URI = "/cart";
    private static final String ITEM_URI = CART_URI + "/cart-items";
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
        beforeEachShoppingCartTest(dataSource);
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Get user's shopping cart")
    void getShoppingCart_GetUsersShoppingCart_ReturnsShoppingCartWithItems() throws Exception {
        // Given
        CartItemResponseDto firstCartItemResponse = getCartItemResponseDto();
        CartItemResponseDto secondCartItemResponse =
                new CartItemResponseDto(ID_2, ID_2, TITLE_MOBI_DICK, 2);
        List<CartItemResponseDto> cartItemResponseDtos =
                List.of(firstCartItemResponse, secondCartItemResponse);
        ShoppingCartDto expected = new ShoppingCartDto(ID_1, ID_1, cartItemResponseDtos);

        // When
        MvcResult result = mockMvc.perform(get(CART_URI)
                                          .with(user(getUser()))
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk())
                                  .andReturn();

        // Then
        ShoppingCartDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        ShoppingCartDto.class);
        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "cartItems"));
        assertTrue(actual.cartItems().containsAll(expected.cartItems()));
    }

    @Test
    @DisplayName("Add new cart item")
    @WithMockUser
    void addCartItem_AddNewCartItem_ReturnsExpectedCartItem() throws Exception {
        // Given
        CartItemRequestDto requestDto = getCartItemRequestDto();
        CartItemResponseDto expected = getCartItemResponseDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(post(CART_URI)
                                          .content(jsonRequest)
                                          .with(user(getUser()))
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isCreated())
                                  .andReturn();

        // Then
        CartItemResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CartItemResponseDto.class);
        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Update cart item quantity by ID")
    @WithMockUser
    void updateCartItem_UpdateCartItemQuantity_ReturnsExpectedQuantity() throws Exception {
        // Given
        String url = ITEM_URI + "/" + ID_1;
        QuantityDto expected = getQuantityDto();
        String jsonRequest = objectMapper.writeValueAsString(expected);

        // When
        MvcResult result = mockMvc.perform(put(url)
                                          .content(jsonRequest)
                                          .with(user(getUser()))
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isAccepted())
                                  .andReturn();

        // Then
        QuantityDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        QuantityDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete a cart item by ID")
    @WithMockUser
    void deleteCartItem_DeleteCartItemById_SuccessfullyDeleted() throws Exception {
        // Given
        String url = ITEM_URI + "/" + ID_1;

        // When / Then
        mockMvc.perform(delete(url)
                       .with(user(getUser()))
                       .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
