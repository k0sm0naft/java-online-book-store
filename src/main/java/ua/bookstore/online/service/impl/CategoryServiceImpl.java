package ua.bookstore.online.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.bookstore.online.dto.category.CategoryRequestDto;
import ua.bookstore.online.dto.category.CategoryResponseDto;
import ua.bookstore.online.exception.EntityNotFoundException;
import ua.bookstore.online.mapper.CategoryMapper;
import ua.bookstore.online.model.Category;
import ua.bookstore.online.repository.category.CategoryRepository;
import ua.bookstore.online.service.CategoryService;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponseDto> findAll() {
        return categoryRepository.findAll().stream()
                                 .map(categoryMapper::toResponseDto)
                                 .toList();
    }

    @Override
    public CategoryResponseDto getById(Long id) {
        return categoryMapper.toResponseDto(
                categoryRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Can't find category by id:" + id)));
    }

    @Override
    public CategoryResponseDto save(CategoryRequestDto categoryDto) {
        Category category = categoryMapper.toModel(categoryDto);
        return categoryMapper.toResponseDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto update(Long id, CategoryRequestDto categoryDto) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find category by id " + id);
        }
        Category category = categoryMapper.toModel(categoryDto);
        category.setId(id);
        return categoryMapper.toResponseDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Can't find book to delete by id " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public Set<Long> getAllExistedCategoryIdsFromIds(Set<Long> ids) {
        return categoryRepository.findAllByIdIn(ids).stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
    }
}
