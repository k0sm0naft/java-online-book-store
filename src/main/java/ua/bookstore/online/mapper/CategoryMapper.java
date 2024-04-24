package ua.bookstore.online.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bookstore.online.config.MapperConfig;
import ua.bookstore.online.dto.category.CategoryRequestDto;
import ua.bookstore.online.dto.category.CategoryResponseDto;
import ua.bookstore.online.model.Category;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    Category toModel(CategoryRequestDto categoryDto);

    CategoryResponseDto toResponseDto(Category category);
}
