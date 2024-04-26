package ua.bookstore.online.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bookstore.online.config.MapperConfig;
import ua.bookstore.online.dto.order.OrderItemResponseDto;
import ua.bookstore.online.model.OrderItem;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemResponseDto toDto(OrderItem orderItem);
}
