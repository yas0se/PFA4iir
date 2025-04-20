package com.example.pfa4iir;

public class ProductItem {
    private String name;
    private String price;
    private String oldPrice;
    private String imageUrl;
    private String productUrl;
    private boolean isAvailable;

    // Constructeur
    public ProductItem(String name, String price, String oldPrice, String imageUrl, String productUrl, boolean isAvailable) {
        this.name = name;
        this.price = price;
        this.oldPrice = oldPrice;
        this.imageUrl = imageUrl;
        this.productUrl = productUrl;
        this.isAvailable = isAvailable;
    }

    // Getters
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getOldPrice() { return oldPrice; }
    public String getImageUrl() { return imageUrl; }
    public String getProductUrl() { return productUrl; }
    public boolean isAvailable() { return isAvailable; }
}