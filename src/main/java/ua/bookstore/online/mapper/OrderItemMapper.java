package ua.bookstore.online.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bookstore.online.config.MapperConfig;
import ua.bookstore.online.dto.order.OrderItemResponseDto;
import ua.bookstore.online.model.CartItem;
import ua.bookstore.online.model.OrderItem;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemResponseDto toDto(OrderItem orderItem);

    default OrderItem toModel(CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setBook(cartItem.getBook());
        orderItem.setPrice(cartItem.getBook().getPrice());
        orderItem.setQuantity(cartItem.getQuantity());
        return orderItem;
    }
}
