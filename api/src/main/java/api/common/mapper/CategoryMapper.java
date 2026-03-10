package api.common.mapper;

import entity.category.Category;

public class CategoryMapper {
    public static Category toCategory(String name){
        return Category.builder()
                .name(name)
                .build();
    }

    public static void updateCategory(Category category, String name){
        category.changeName(name);
    }
}
