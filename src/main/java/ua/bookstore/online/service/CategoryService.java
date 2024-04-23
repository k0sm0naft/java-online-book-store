package ua.bookstore.online.service;

import java.util.List;
import java.util.Set;
import ua.bookstore.online.dto.category.CategoryRequestDto;
import ua.bookstore.online.dto.category.CategoryResponseDto;

public interface CategoryService {
    List<CategoryResponseDto> findAll();

    CategoryResponseDto getById(Long id);

    CategoryResponseDto save(CategoryRequestDto categoryDto);

    CategoryResponseDto update(Long id, CategoryRequestDto categoryDto);

    void deleteById(Long id);

    Set<Long> getAllExistedCategoryIdsFromIds(Set<Long> ids);
}
