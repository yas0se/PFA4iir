package com.example.pfa4iir;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class UltraPcScraper {
    private static final String TAG = "WebScraper";
    private static final int MAX_RETRIES = 3;
    private static final int TIMEOUT = 30000; // 30 seconds

    public static List<ProductItem> scrapeProducts(String searchQuery) throws IOException {
        List<ProductItem> products = new ArrayList<>();
        String query=URLEncoder.encode(searchQuery, "UTF-8");
        String url = "https://ultrapc.ma/recherche?controller=search&s=" + query;

        Log.i(TAG, "═════════ STARTING SCRAPE ═════════");

        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                attempt++;
                Log.d(TAG, "Attempt #" + attempt + " for URL: " + url);

                Log.w(TAG,"vavavalueeeee: 1111111111");

                // Connect directly with Jsoup
                Document doc = Jsoup.connect(url)
//                Document doc = Jsoup.connect("https://www.ultrapc.ma/recherche?controller=search&s=cpu")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                        .timeout(6000)
                        .get();
                Log.w(TAG,"vavavalueeeee: 22222222");

                Elements productElements = doc.select("article.product-miniature");

                if (productElements.isEmpty()) {
                    Log.w(TAG, "No products found using primary selector");
                    productElements = doc.select("div.js-product-miniature, div.product-container");
                }

                Log.d(TAG, "Found " + productElements.size() + " products");

                for (Element product : productElements) {
                    try {
                        // Extract product name (handle truncated names)
                        String name = product.select("h3.product-title a").text();
//                        if (name.contains("...")) {
//                            name = product.select("h3.product-title a").attr("title");
//                        }

                        // Extract price (handle both displayed price and content attribute)
                        String price = product.select("span.price").text();
                        if (price.isEmpty()) {
                            price = product.select("span[itemprop=price]").attr("content") + " MAD";
                        }

                        // Extract image URL (handle both src and data-full-size-image-url)
                        String imageUrl = product.select("img").attr("src");
                        if (imageUrl.isEmpty() || imageUrl.contains("home_default")) {
                            String largeImage = product.select("img").attr("data-full-size-image-url");
                            if (!largeImage.isEmpty()) {
                                imageUrl = largeImage;
                            }
                        }

                        // Extract product URL
                        String productUrl = product.select("h3.product-title a").attr("href");

                        // Check availability
                        boolean isAvailable = !product.select("div.product-availability").text()
                                .toLowerCase().contains("rupture");

                        // Fix relative URLs
                        if (imageUrl != null && imageUrl.startsWith("/")) {
                            imageUrl = "https://www.ultrapc.ma" + imageUrl;
                        }
                        if (productUrl != null && productUrl.startsWith("/")) {
                            productUrl = "https://www.ultrapc.ma" + productUrl;
                        }

                        String promoText = product.select("li.product-flag.discount").text(); // ou autre classe selon ton site
                        String discountText = product.select("li.product-flag.promo").text();

                        // Validate and add product
                        if (!name.isEmpty() && !price.isEmpty() && !price.equals("0,00 MAD")) {
                            products.add(new ProductItem(name, price, imageUrl, productUrl, isAvailable, promoText, discountText));
                        }
                        // In UltraPcScraper.scrapeProducts()
                        Log.d("Scraper", "Parsed product: " + name + " | " + price + " | " + imageUrl);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing product", e);
                    }
                }

                if (!products.isEmpty()) {
                    Log.i(TAG, "Successfully scraped " + products.size() + " products");
                    return products;
                }

            } catch (IOException e) {
                Log.e(TAG, "Attempt " + attempt + " failed", e);
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(2000 * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        Log.w(TAG, "Failed after " + MAX_RETRIES + " attempts");
        return products;
    }
}