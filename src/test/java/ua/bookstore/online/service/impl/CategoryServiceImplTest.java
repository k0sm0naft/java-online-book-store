package ua.bookstore.online.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_1;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_2;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_3;
import static ua.bookstore.online.utils.ConstantAndMethod.createCategory;
import static ua.bookstore.online.utils.ConstantAndMethod.getCategoryRequest;
import static ua.bookstore.online.utils.ConstantAndMethod.getFiction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ua.bookstore.online.dto.category.CategoryRequestDto;
import ua.bookstore.online.dto.category.CategoryResponseDto;
import ua.bookstore.online.exception.EntityNotFoundException;
import ua.bookstore.online.mapper.CategoryMapper;
import ua.bookstore.online.model.Category;
import ua.bookstore.online.repository.category.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @AfterEach
    void afterEach() {
        // Verify method calls
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Get all categories from DB, returns list of CategoryResponseDto")
    void findAll_ReturnsListOfCategoryResponseDto() {
        // Given
        Pageable pageable = Pageable.unpaged();
        List<Category> categoriesFromDb = List.of(createCategory(), createCategory());
        PageImpl<Category> categories = new PageImpl<>(categoriesFromDb);

        // Mocking behavior
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categories);
        when(categoryMapper.toResponseDto(any(Category.class))).thenReturn(getFiction());

        // When
        List<CategoryResponseDto> actual = categoryService.findAll(pageable);

        // Then
        assertEquals(categoriesFromDb.size(), actual.size());
    }

    @Test
    @DisplayName("Get existing category by id")
    void getById_ExistingCategory_ReturnsCategoryResponseDto() {
        // Given
        Category existingCategory = createCategory();
        existingCategory.setId(ID_1);
        CategoryResponseDto responseDto = getFiction();

        // Mocking behavior
        when(categoryRepository.findById(ID_1)).thenReturn(Optional.of(existingCategory));
        when(categoryMapper.toResponseDto(existingCategory)).thenReturn(responseDto);

        // When
        CategoryResponseDto actual = categoryService.getById(ID_1);

        // Then
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual);
    }

    @Test
    @DisplayName("Get non-existing category by id, throws exception")
    void getById_NonExistingCategory_ThrowsException() {
        // Mocking behavior
        when(categoryRepository.findById(ID_1)).thenReturn(java.util.Optional.empty());

        // When/Then
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(ID_1));

        assertEquals("Can't find category by id:" + ID_1, exception.getMessage());
    }

    @Test
    @DisplayName("Save new category successfully")
    void save_NewCategory_ReturnsCategoryResponseDto() {
        // Given
        CategoryRequestDto requestDto = getCategoryRequest();
        Category categoryToSave = createCategory();
        Category savedCategory = createCategory();
        savedCategory.setId(ID_1);

        // Mocking behavior
        when(categoryMapper.toModel(requestDto)).thenReturn(categoryToSave);
        when(categoryRepository.save(categoryToSave)).thenReturn(savedCategory);
        when(categoryMapper.toResponseDto(savedCategory)).thenReturn(getFiction());

        // When
        CategoryResponseDto actual = categoryService.save(requestDto);

        // Then
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(requestDto, actual);
    }

    @Test
    @DisplayName("Update existing category successfully")
    void update_ExistingCategory_ReturnsCategoryResponseDto() {
        // Given
        CategoryRequestDto requestDto = getCategoryRequest();
        Category updatedCategory = createCategory();
        updatedCategory.setId(ID_1);
        CategoryResponseDto responseDto = getFiction();

        // Mocking behavior
        when(categoryRepository.existsById(ID_1)).thenReturn(true);
        when(categoryMapper.toModel(requestDto)).thenReturn(updatedCategory);
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);
        when(categoryMapper.toResponseDto(updatedCategory)).thenReturn(responseDto);

        // When
        CategoryResponseDto actual = categoryService.update(ID_1, requestDto);

        // Then
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual);
    }

    @Test
    @DisplayName("Update non-existing category, throws exception")
    void update_NonExistingCategory_ThrowsException() {
        // Mocking behavior
        when(categoryRepository.existsById(ID_1)).thenReturn(false);

        // When
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(ID_1, getCategoryRequest()));

        // Then
        assertEquals("Can't find category by id " + ID_1, actual.getMessage());
    }

    @Test
    @DisplayName("Delete existing category by ID")
    void deleteById_ExistingCategory_Ok() {
        // Mocking behavior
        when(categoryRepository.findById(ID_1)).thenReturn(Optional.of(createCategory()));
        doNothing().when(categoryRepository).deleteById(ID_1);

        // When
        assertDoesNotThrow(() -> categoryService.deleteById(ID_1));
    }

    @Test
    @DisplayName("Delete non-existing category by ID, throws exception")
    void deleteById_NonExistingCategory_ThrowsException() {
        // Mocking behavior
        when(categoryRepository.findById(ID_1)).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(ID_1));

        // Then
        assertEquals("Can't find category to delete by id " + ID_1, exception.getMessage());
    }

    @Test
    @DisplayName("Get all existing category IDs from provided IDs")
    void getAllExistedCategoryIdsFromIds_ValidIds_ReturnsExistingCategoryIds() {
        // Given
        Set<Long> categoryIds = Set.of(ID_1, ID_2, ID_3);
        Set<Category> existingCategories = Set.of(
                new Category(ID_1),
                new Category(ID_2)
        );

        // Mocking behavior
        when(categoryRepository.findAllByIdIn(categoryIds)).thenReturn(existingCategories);

        // When
        Set<Long> actual = categoryService.getAllExistedCategoryIdsFromIds(categoryIds);

        // Then
        Set<Long> expected = existingCategories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get all existing category IDs from empty list of IDs")
    void getAllExistedCategoryIdsFromIds_EmptyIds_ReturnsEmptySet() {
        // Given
        Set<Long> emptyCategoryIds = Collections.emptySet();

        // Mocking behavior
        when(categoryRepository.findAllByIdIn(emptyCategoryIds)).thenReturn(Collections.emptySet());

        // When
        Set<Long> actual = categoryService.getAllExistedCategoryIdsFromIds(emptyCategoryIds);

        // Then
        assertTrue(actual.isEmpty(), "Should be empty but was not");
    }
}
