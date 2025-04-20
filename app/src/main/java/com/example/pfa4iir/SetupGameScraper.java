//package com.example.pfa4iir;
//
//import android.util.Log;
//
//import okhttp3.*;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//public class SetupGameScraper {
//    private static final String TAG = "WebScraper";
//    private static final OkHttpClient client = new OkHttpClient.Builder()
//            .build();
//
//    public static List<ProductItem> scrapeProducts(String searchQuery) {
//        List<ProductItem> products = new ArrayList<>();
//        String url = "https://ultrapc.ma/recherche?controller=search&s=" +
//                encodeQuery(searchQuery);
//
//        try {
//            Log.d(TAG, "Starting scrape for: " + searchQuery);
//
//            Request request = buildRequest(url);
//            Response response = executeRequest(request);
//            Document doc = parseResponse(response);
//
//            products = extractProducts(doc);
//            Log.i(TAG, "Successfully scraped " + products.size() + " products");
//
//        } catch (Exception e) {
//            Log.e(TAG, "Scraping failed", e);
//        }
//
//        return products;
//    }
//
//    private static String encodeQuery(String query) {
//        try {
//            return java.net.URLEncoder.encode(query, StandardCharsets.UTF_8.name());
//        } catch (Exception e) {
//            Log.w(TAG, "URL encoding failed, using raw query");
//            return query;
//        }
//    }
//
//    private static Request buildRequest(String url) {
//        return new Request.Builder()
//                .url(url)
//                .header("User-Agent", "Mozilla/5.0...")
//                .build();
//    }
//
//    private static Response executeRequest(Request request) throws IOException {
//        Log.d(TAG, "Executing request to: " + request.url());
//        Response response = client.newCall(request).execute();
//
//        if (!response.isSuccessful()) {
//            String error = "HTTP " + response.code() + " - " + response.message();
//            Log.e(TAG, error);
//            throw new IOException(error);
//        }
//        return response;
//    }
//
//    private static Document parseResponse(Response response) throws IOException {
//        try {
//            String body = response.body().string();
//            Log.v(TAG, "Response body length: " + body.length());
//            return Jsoup.parse(body, response.request().url().toString());
//        } catch (IOException e) {
//            Log.e(TAG, "Failed to parse response", e);
//            throw e;
//        }
//    }
//
//    private static List<ProductItem> extractProducts(Document doc) {
//        List<ProductItem> products = new ArrayList<>();
//        Elements productElements = doc.select("div.product-miniature");
//
//        for (Element product : productElements) {
//            String name = product.select("h3.product-title a").text();
//            String price = product.select("span.price").text();
//            String oldPrice = product.select("span.regular-price").text();
//            String imageUrl = product.select("img.product-thumbnail").attr("src");
//            String productUrl = product.select("h3.product-title a").attr("href");
//            boolean isAvailable = !product.select("div.product-availability").text().contains("Rupture");
//
//            // Fix relative URLs
//            if (imageUrl.startsWith("/")) imageUrl = "https://ultrapc.ma" + imageUrl;
//            if (productUrl.startsWith("/")) productUrl = "https://ultrapc.ma" + productUrl;
//
//            products.add(new ProductItem(name, price, oldPrice, imageUrl, productUrl, isAvailable));
//        }
//
//        return products;
//    }
//}
//
//class LoggingInterceptor1 implements Interceptor {
//    @Override
//    public Response intercept(Chain chain) throws IOException {
//        Request request = chain.request();
//
//        long startTime = System.nanoTime();
//        Log.d("OkHttp", String.format("Sending request %s%n%s",
//                request.url(), request.headers()));
//
//        try {
//            Response response = chain.proceed(request);
//
//            long endTime = System.nanoTime();
//            Log.d("OkHttp", String.format("Received response for %s in %.1fms%n%s",
//                    response.request().url(), (endTime - startTime) / 1e6d, response.headers()));
//
//            return response;
//        } catch (Exception e) {
//            Log.e("OkHttp", "Request failed: " + e.getMessage());
//            throw e;
//        }
//    }
//}
//class OkHttpUtil {
//    public static String urlEncode(String value) {
//        try {
//            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
//        } catch (Exception e) {
//            return value;
//        }
//    }
//}
//
//class RetryInterceptor implements Interceptor {
//    private final int maxRetries;
//
//    public RetryInterceptor(int maxRetries) {
//        this.maxRetries = maxRetries;
//    }
//
//    @Override
//    public Response intercept(Chain chain) throws IOException {
//        Request request = chain.request();
//        Response response = null;
//        IOException exception = null;
//
//        for (int i = 0; i <= maxRetries; i++) {
//            try {
//                response = chain.proceed(request);
//                if (response.isSuccessful()) {
//                    return response;
//                }
//            } catch (IOException e) {
//                exception = e;
//            }
//
//            if (i < maxRetries) {
//                try {
//                    Thread.sleep(2000 * (i + 1));
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
//            }
//        }
//
//        if (exception != null) throw exception;
//        if (response != null) return response;
//        throw new IOException("Unknown error occurred");
//    }
//}
