package ua.bookstore.online.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bookstore.online.config.MapperConfig;
import ua.bookstore.online.dto.BookDto;
import ua.bookstore.online.dto.CreateBookRequestDto;
import ua.bookstore.online.model.Book;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    @Mapping(target = "id", ignore = true)
    Book toModel(CreateBookRequestDto requestDto);
}
