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


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UltraPcScraper {
    private static final String TAG = "UltraPcScraper";
    private static final int MAX_RETRIES = 2;
    private static final int TIMEOUT_MS = 30000; // 30 seconds
    private static final String BASE_URL = "https://www.ultrapc.ma";
    private static final String BASE_URL1 = "https://www.nextlevelpc.ma";
    private static final String BASE_URL2 = "https://www.techspace.ma";

    public static List<ProductItem> scrapeProducts(String searchQuery) throws IOException {
        List<ProductItem> products = new ArrayList<>();
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .build();

        String encodedQuery = URLEncoder.encode(searchQuery, "UTF-8");
//        String url = BASE_URL + "/recherche?controller=search&s=" + encodedQuery;

        Log.i(TAG, "═════════ STARTING SCRAPE FOR: " + searchQuery + " ═════════");

        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            attempt++;
            String url = BASE_URL + "/recherche?controller=search&s=" + encodedQuery;

            Log.d(TAG, "Attempt #" + attempt + " for URL: " + url);

            try {
                Request request = new Request.Builder()
                        .url(url)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "en-US,en;q=0.5")
                        .header("Connection", "keep-alive")
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Failed to fetch page: HTTP " + response.code());
                        continue;
                    }

                    String html = response.body().string();
                    Document doc = Jsoup.parse(html);
                    Elements productElements = doc.select("article.product-miniature");

                    if (productElements.isEmpty()) {
                        Log.w(TAG, "No products found with primary selector. Trying fallback selectors...");
                        productElements = doc.select("div.js-product-miniature, div.product-container");
                    }else {
                        attempt++;
                    }

                    Log.d(TAG, "Found " + productElements.size() + " product elements");

                    for (Element product : productElements) {
                        try {
                            String name = product.select("h3.product-title a").text();
                            String price = product.select("span.price").text();

                            if (price.isEmpty()) {
                                price = product.select("span[itemprop=price]").attr("content") + " MAD";
                            }

                            String imageUrl = product.select("img").attr("src");
                            if (imageUrl.isEmpty() || imageUrl.contains("home_default")) {
                                imageUrl = product.select("img").attr("data-full-size-image-url");
                            }

                            String productUrl = product.select("h3.product-title a").attr("href");

                            boolean isAvailable = !product.select("div.product-availability").text()
                                    .toLowerCase().contains("rupture");

                            if (imageUrl != null && imageUrl.startsWith("/")) {
                                imageUrl = "https://www.ultrapc.ma" + imageUrl;
                            }
                            if (productUrl != null && productUrl.startsWith("/")) {
                                productUrl = "https://www.ultrapc.ma" + productUrl;
                            }

                            String promoText = product.select("li.product-flag.discount").text();
                            String discountText = product.select("li.product-flag.promo").text();

                            if (!name.isEmpty() && !price.isEmpty() && !price.equals("0,00 MAD")) {
                                products.add(new ProductItem(name, price, imageUrl, productUrl, isAvailable, promoText, discountText));
                            }

                            Log.d(TAG, "UltraPc Parsed product: " + name + " | " + price + " | " + productUrl);

                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing product", e);
                        }
                    }

                    if (!products.isEmpty()) {
                        Log.i(TAG, "Successfully scraped " + products.size() + " products");
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
            } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                Log.w(TAG, "Failed after " + MAX_RETRIES + " attempts");
        }

      /// ////////////////////////////////

        attempt=0;
        while (attempt < MAX_RETRIES) {
            attempt++;
            String url = BASE_URL1 + "/recherche?controller=search&s=" + encodedQuery;

            Log.d(TAG, "NextLevelPC Attempt #" + attempt + " for URL: " + url);

            try {
                Request request = new Request.Builder()
                        .url(url)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "en-US,en;q=0.5")
                        .header("Connection", "keep-alive")
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "NextLevelPC Failed to fetch page: HTTP " + response.code());
                        continue;
                    }

                    String html = response.body().string();
                    Document doc = Jsoup.parse(html);
                    Elements productElements = doc.select("article.item.product-miniature"); // Primary selector for NextLevelPC

                    if (productElements.isEmpty()) {
                        Log.w(TAG, "NextLevelPC: No products found with primary selector. Trying fallback...");
                        productElements = doc.select("div.product-miniature"); // Fallback selector
                    }

                    Log.d(TAG, "NextLevelPC Found " + productElements.size() + " product elements");

                    for (Element product : productElements) {
                        try {
                            // Extract product name
                            String name = product.select("h2[itemprop=name], h2.product-title, h3.product-title").text();
                            if (name.isEmpty()) {
                                name = product.select("a[itemprop=url]").attr("title");
                            }

                            // Extraction du prix - version corrigée
                            String price = "";

// Méthode 1: Sélection précise du premier élément prix trouvé
                            Elements priceElements = product.select("div.product-price-and-shipping span.price");
                            if (!priceElements.isEmpty()) {
                                price = priceElements.first().text().trim();

                                // Nettoyer le prix si nécessaire (supprimer les doublons)
                                if (price.contains(" ")) {
                                    price = price.split(" ")[0]; // Prend seulement la première occurrence
                                }
                            }

// Méthode alternative plus stricte (si la première ne fonctionne pas)
                            if (price.isEmpty()) {
                                Element priceElement = product.select("div.tv-product-price span.price").first();
                                if (priceElement != null) {
                                    price = priceElement.text().trim();
                                }
                            }

// Si toujours vide, essayer d'autres sélecteurs
                            if (price.isEmpty()) {
                                price = product.select("span[itemprop=price]").attr("content") + " MAD";
                            }
                            price = price+" MAD";

// Validation finale
                            if (price.isEmpty() || price.equals("0,00 MAD")) {
                                price = "";
                            }


                            // Extract image URL
                            String imageUrl = product.select("img[itemprop=image]").attr("src");
                            if (imageUrl.isEmpty()) {
                                imageUrl = product.select("img.tvproduct-defult-img").attr("src");
                            }
                            if (imageUrl.isEmpty()) {
                                imageUrl = product.select("img").first().attr("src");
                            }

                            // Extract product URL
                            String productUrl = product.select("a[itemprop=url]").attr("href");
                            if (productUrl.isEmpty()) {
                                productUrl = product.select("h2.product-title a, h3.product-title a").attr("href");
                            }

                            // Check availability
                            boolean isAvailable = product.select("div.custom-product-badge span.badge-name-text")
                                    .text()
                                    .toLowerCase().contains("en stock");

                            // Extract promo/discount info
                            String promoText = "";
                            String discountText = "";
                            if (discountText.isEmpty()) {
                                discountText = product.select("span.discount").text();
                            }

                            // Fix relative URLs
                            if (imageUrl != null && imageUrl.startsWith("/")) {
                                imageUrl = "https://www.nextlevelpc.ma" + imageUrl;
                            }
                            if (productUrl != null && productUrl.startsWith("/")) {
                                productUrl = "https://www.nextlevelpc.ma" + productUrl;
                            }

                            // Add product if valid
                            if (!name.isEmpty() && !price.isEmpty() && !price.equals("0,00 MAD")) {
                                products.add(new ProductItem(name, price, imageUrl, productUrl, isAvailable, promoText, discountText));
                                Log.d(TAG, "NextLevelPC Parsed product: " + name);
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "NextLevelPC Error parsing product", e);
                        }
                    }

                    if (!products.isEmpty()) {
                        Log.i(TAG, "NextLevelPC Successfully scraped " + products.size() + " products");
                        return products;
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "NextLevelPC Attempt " + attempt + " failed", e);
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(2000 * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return products;
                    }
                }
            }
        }

        return products;
    }
}
