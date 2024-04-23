package ua.bookstore.online.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ua.bookstore.online.config.MapperConfig;
import ua.bookstore.online.dto.book.BookDto;
import ua.bookstore.online.dto.book.BookDtoWithoutCategoryIds;
import ua.bookstore.online.dto.book.CreateBookRequestDto;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.model.Category;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    @Mapping(target = "categoryIds", source = "categories", qualifiedByName = "setCategoryIds")
    BookDto toDto(Book book);

    @Named("setCategoryIds")
    default Set<Long> setCategoryIds(Set<Category> categories) {
        return categories.stream()
                         .map(Category::getId)
                         .collect(Collectors.toSet());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", source = "categoryIds", qualifiedByName = "categoryById")
    Book toModel(CreateBookRequestDto requestDto);

    @Named("categoryById")
    default Set<Category> categoryById(Set<Long> categoryIds) {
        return categoryIds.stream()
                          .map(Category::new)
                          .collect(Collectors.toSet());
    }

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);
}
