package ua.bookstore.online.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.EqualsBuilder;
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
    private static final long ID = 5L;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("Get all categories from DB, returns list of CategoryResponseDto")
    void findAll_ReturnsListOfCategoryResponseDto() {
        // Given
        Pageable pageable = Pageable.unpaged();
        List<Category> categoriesFromDb = List.of(createCategory(), createCategory());
        PageImpl<Category> categories = new PageImpl<>(categoriesFromDb);

        // Mocking behavior
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categories);
        when(categoryMapper.toResponseDto(any(Category.class))).thenReturn(
                getResponseDto(createCategory()));

        // When
        List<CategoryResponseDto> actual = categoryService.findAll(pageable);

        // Then
        assertEquals(categoriesFromDb.size(), actual.size());

        // Verify method calls
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper, times(categoriesFromDb.size())).toResponseDto(any(Category.class));
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Get existing category by id")
    void getById_ExistingCategory_ReturnsCategoryResponseDto() {
        // Given
        Category existingCategory = createCategory();
        existingCategory.setId(ID);
        CategoryResponseDto responseDto = getResponseDto(existingCategory);

        // Mocking behavior
        when(categoryRepository.findById(ID)).thenReturn(Optional.of(existingCategory));
        when(categoryMapper.toResponseDto(existingCategory)).thenReturn(responseDto);

        // When
        CategoryResponseDto actual = categoryService.getById(ID);

        // Then
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual);

        // Verify method calls
        verify(categoryRepository).findById(ID);
        verify(categoryMapper).toResponseDto(existingCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Get non-existing category by id, throws exception")
    void getById_NonExistingCategory_ThrowsException() {
        // Mocking behavior
        when(categoryRepository.findById(ID)).thenReturn(java.util.Optional.empty());

        // When/Then
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(ID));

        assertEquals("Can't find category by id:" + ID, exception.getMessage());

        // Verify method calls
        verify(categoryRepository).findById(ID);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Save new category successfully")
    void save_NewCategory_ReturnsCategoryResponseDto() {
        // Given
        CategoryRequestDto requestDto = createRequestDto();
        Category categoryToSave = createCategory();
        Category savedCategory = createCategory();
        savedCategory.setId(ID);
        CategoryResponseDto responseDto = getResponseDto(categoryToSave);

        // Mocking behavior
        when(categoryMapper.toModel(requestDto)).thenReturn(categoryToSave);
        when(categoryRepository.save(categoryToSave)).thenReturn(savedCategory);
        when(categoryMapper.toResponseDto(savedCategory)).thenReturn(responseDto);

        // When
        CategoryResponseDto actual = categoryService.save(requestDto);

        // Then
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(requestDto, actual);

        // Verify method calls
        verify(categoryMapper).toModel(requestDto);
        verify(categoryRepository).save(categoryToSave);
        verify(categoryMapper).toResponseDto(savedCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Update existing category successfully")
    void update_ExistingCategory_ReturnsCategoryResponseDto() {
        // Given
        CategoryRequestDto requestDto = createRequestDto();
        Category updatedCategory = createCategory();
        updatedCategory.setId(ID);
        CategoryResponseDto responseDto = getResponseDto(updatedCategory);

        // Mocking behavior
        when(categoryRepository.existsById(ID)).thenReturn(true);
        when(categoryMapper.toModel(requestDto)).thenReturn(updatedCategory);
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);
        when(categoryMapper.toResponseDto(updatedCategory)).thenReturn(responseDto);

        // When
        CategoryResponseDto actual = categoryService.update(ID, requestDto);

        // Then
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(responseDto, actual);

        // Verify method calls
        verify(categoryRepository).existsById(ID);
        verify(categoryMapper).toModel(requestDto);
        verify(categoryRepository).save(updatedCategory);
        verify(categoryMapper).toResponseDto(updatedCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Update non-existing category, throws exception")
    void update_NonExistingCategory_ThrowsException() {
        // Mocking behavior
        when(categoryRepository.existsById(ID)).thenReturn(false);

        // When
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(ID, createRequestDto()));

        // Then
        assertEquals("Can't find category by id " + ID, actual.getMessage());

        // Verify method calls
        verify(categoryRepository).existsById(ID);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Delete existing category by ID")
    void deleteById_ExistingCategory_Ok() {
        // Mocking behavior
        when(categoryRepository.findById(ID)).thenReturn(Optional.of(createCategory()));

        // When
        assertDoesNotThrow(() -> categoryService.deleteById(ID));

        // Verify method calls
        verify(categoryRepository).findById(ID);
        verify(categoryRepository).deleteById(ID);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Delete non-existing category by ID, throws exception")
    void deleteById_NonExistingCategory_ThrowsException() {
        // Mocking behavior
        when(categoryRepository.findById(ID)).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(ID));

        // Then
        assertEquals("Can't find category to delete by id " + ID, exception.getMessage());

        // Verify method calls
        verify(categoryRepository).findById(ID);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Get all existing category IDs from provided IDs")
    void getAllExistedCategoryIdsFromIds_ValidIds_ReturnsExistingCategoryIds() {
        // Given
        Set<Long> categoryIds = Set.of(1L, 2L, 3L);
        Set<Category> existingCategories = Set.of(
                new Category(1L),
                new Category(2L)
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

        // Verify method calls
        verify(categoryRepository).findAllByIdIn(categoryIds);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Get all existing category IDs from empty list of IDs")
    void getAllExistedCategoryIdsFromIds_EmptyIds_ReturnsEmptySet() {
        // Given
        Set<Long> emptyCategoryIds = Collections.emptySet();

        // When
        Set<Long> actual = categoryService.getAllExistedCategoryIdsFromIds(emptyCategoryIds);

        // Then
        assertTrue(actual.isEmpty(), "Should be empty but was not");

        // Verify method calls
        verify(categoryRepository).findAllByIdIn(emptyCategoryIds);
        verifyNoMoreInteractions(categoryRepository);
    }

    private CategoryRequestDto createRequestDto() {
        return CategoryRequestDto.builder()
                                 .name("name")
                                 .description("description")
                                 .build();
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName("name");
        category.setDescription("description");
        return category;
    }

    private CategoryResponseDto getResponseDto(Category category) {
        return CategoryResponseDto.builder()
                                  .id(category.getId())
                                  .name(category.getName())
                                  .description(category.getDescription())
                                  .build();
    }
}
