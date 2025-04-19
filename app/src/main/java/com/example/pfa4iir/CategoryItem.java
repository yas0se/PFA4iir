package com.example.pfa4iir;

// CategoryItem.java
public class CategoryItem {
    private String categoryName;
    private int imageResId;

    public CategoryItem(String categoryName, int imageResId) {
        this.categoryName = categoryName;
        this.imageResId = imageResId;
    }

    // Getters
    public String getCategoryName() { return categoryName; }
    public int getImageResId() { return imageResId; }
}
