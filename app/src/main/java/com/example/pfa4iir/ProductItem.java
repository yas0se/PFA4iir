package com.example.pfa4iir;

public class ProductItem {
    private String name;
    private String price;
    private String imageUrl;
    private String productUrl;
    private boolean isAvailable;
    private String promoText;
    private String discountText;

    // Constructeur
    public ProductItem(String name, String price, String imageUrl,
                       String productUrl, boolean isAvailable, String promoText, String discountText) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.productUrl = productUrl;
        this.isAvailable = isAvailable;
        this.promoText = promoText;
        this.discountText = discountText;
    }

    // Getters
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getProductUrl() { return productUrl; }
    public boolean isAvailable() { return isAvailable; }
    public String getPromoText() { return promoText; }
    public String getDiscountText() { return discountText; }
}
