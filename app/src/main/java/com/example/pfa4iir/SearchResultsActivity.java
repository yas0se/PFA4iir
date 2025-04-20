package com.example.pfa4iir;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Initialize views
        String initialQuery = getIntent().getStringExtra("search_query");
        Toast.makeText(this, "testtttt: " + initialQuery, Toast.LENGTH_SHORT).show();


        recyclerView = findViewById(R.id.productsRecyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        searchInput = findViewById(R.id.navSearchKeywords);
        ImageButton searchButton = findViewById(R.id.navSearchSubmit);

        // Setup RecyclerView with empty adapter first
        adapter = new ProductAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));  // Fixed context reference
        recyclerView.setAdapter(adapter);

        Log.i("query è--è--è--: ", searchInput.getText().toString());

// Initial search
        searchProducts(searchInput.getText().toString());  // Removed named parameter (query:)

// Button de recherche
        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString();
            if (!searchInput.getText().toString().isEmpty()) {
                searchProducts(query);
            }
        });

        // Pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            String query = searchInput.getText().toString();
            searchProducts(query.isEmpty() ? "ordinateur" : query);
        });
    }

    private void searchProducts(String query) {
        swipeRefreshLayout.setRefreshing(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                List<ProductItem> products = UltraPcScraper.scrapeProducts(query);

                handler.post(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (products == null || products.isEmpty()) {
                        showToast("Aucun produit trouvé");
                        adapter.updateProducts(new ArrayList<>());
                        // Show an empty state view here if you have one
                        return;
                    }
                    Log.d("Scraper", "Received " + products.size() + " products");
                    adapter.updateProducts(products);
                });
            } catch (Exception e) {
                handler.post(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    showToast("Erreur: " + e.getMessage());
                    Log.e("Scraper", "Search failed", e);
                });
            } finally {
                executor.shutdown();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(SearchResultsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}