package ru.practicum.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CategoryMapper {
    public CategoryDto categoryToDto(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category не может быть null");
        }
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category dtoToCategory(NewCategoryDto newCategoryDto) {
        if (newCategoryDto == null) {
            throw new IllegalArgumentException("NewCategoryDto не может быть null");
        }
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public List<CategoryDto> listCategoriesToListDto(List<Category> categories) {
        return categories.stream().map(CategoryMapper::categoryToDto).collect(Collectors.toList());
    }
}
