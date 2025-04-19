package com.example.pfa4iir;

public class ProductItem {
    private String id;
    private String title;
    private int imageResId; // Ou String pour une URL
    private String currentPrice;
    private String oldPrice;
    private boolean isPromo;
    private String promoText;
    private String discountText;
    private boolean isAvailable;
    private String availabilityText;

    // Constructeur
    public ProductItem(String id, String title, int imageResId, String currentPrice,
                       String oldPrice, boolean isPromo, String promoText,
                       String discountText, boolean isAvailable, String availabilityText) {
        this.id = id;
        this.title = title;
        this.imageResId = imageResId;
        this.currentPrice = currentPrice;
        this.oldPrice = oldPrice;
        this.isPromo = isPromo;
        this.promoText = promoText;
        this.discountText = discountText;
        this.isAvailable = isAvailable;
        this.availabilityText = availabilityText;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getImageResId() { return imageResId; }
    public String getCurrentPrice() { return currentPrice; }
    public String getOldPrice() { return oldPrice; }
    public boolean isPromo() { return isPromo; }
    public String getPromoText() { return promoText; }
    public String getDiscountText() { return discountText; }
    public boolean isAvailable() { return isAvailable; }
    public String getAvailabilityText() { return availabilityText; }
}