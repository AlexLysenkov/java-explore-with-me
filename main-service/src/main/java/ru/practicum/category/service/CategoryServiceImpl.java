package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.utils.CustomPageRequest;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private static final String CATEGORY_NOT_FOUND = "Категория с id: %d не найдена";


    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.dtoToCategory(newCategoryDto);
        log.info("Категория с id: {} создана", category.getId());
        return CategoryMapper.categoryToDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format(CATEGORY_NOT_FOUND, id)));
        category.setName(categoryDto.getName());
        log.info("Категория с id: {} обновлена", id);
        return CategoryMapper.categoryToDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        CategoryDto categoryDto = CategoryMapper.categoryToDto(categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format(CATEGORY_NOT_FOUND, id))));
        log.info("Категория с id: {} получена", id);
        return categoryDto;
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        Pageable pageable = new CustomPageRequest(from / size, size);
        log.info("Получен список категорий с параметрами from = {}, size = {}", from, size);
        return CategoryMapper.listCategoriesToListDto(categoryRepository.findAll(pageable).toList());
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        checkCategoryExist(id);
        categoryRepository.deleteById(id);
        log.info("Категория с id: {} удалена", id);
    }

    private void checkCategoryExist(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ObjectNotFoundException(String.format(CATEGORY_NOT_FOUND, id));
        }
    }
}
