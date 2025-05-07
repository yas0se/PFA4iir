package com.example.pfa4iir;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SearchResultsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText searchInput;
    private List<ProductItem> currentProducts = new ArrayList<>(); // Garde une référence des produits courants

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Initialisation des vues
        recyclerView = findViewById(R.id.productsRecyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        searchInput = findViewById(R.id.navSearchKeywords);
        ImageButton searchButton = findViewById(R.id.navSearchSubmit);

        // Configuration du RecyclerView
        adapter = new ProductAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        // Gestion des données initiales
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("myObjectList")) {
            List<ProductItem> initialProducts = (List<ProductItem>) intent.getSerializableExtra("myObjectList");
            if (initialProducts != null && !initialProducts.isEmpty()) {
                currentProducts = new ArrayList<>(initialProducts);
                adapter.updateProducts(currentProducts);
            }
        }

        // Bouton de recherche
        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString();
            if (!query.isEmpty()) {
                searchProducts(query);
            }
        });

        // Pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            String query = searchInput.getText().toString();
            if (!query.isEmpty()) {
                searchProducts(query);
            } else {
                showToast("Veuillez entrer une recherche");
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Configuration du Spinner
        Spinner spinner = findViewById(R.id.planets_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.planets_array,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (currentProducts.isEmpty()) return;

        List<ProductItem> sortedProducts = new ArrayList<>(currentProducts); // Copie pour le tri

        switch (position) {
            case 0: // Pertinence (ordre original)
                // Pas de tri nécessaire, on utilise la liste telle quelle
                break;
            case 1: // Prix croissant
                sortedProducts.sort(Comparator.comparingDouble(p -> parsePrice(p.getPrice())));
                break;
            case 2: // Prix décroissant
                sortedProducts.sort((p1, p2) -> Double.compare(
                        parsePrice(p2.getPrice()),
                        parsePrice(p1.getPrice())
                ));
                break;
        }

        adapter.updateProducts(sortedProducts);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Optionnel
    }

    private void searchProducts(String query) {
        swipeRefreshLayout.setRefreshing(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                List<ProductItem> products = UltraPcScraper.scrapeProducts(query);
                currentProducts = new ArrayList<>(products); // Sauvegarde les produits

                handler.post(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (products == null || products.isEmpty()) {
                        showToast("Aucun produit trouvé");
                        adapter.updateProducts(new ArrayList<>());
                        return;
                    }
                    adapter.updateProducts(products);
                });
            } catch (Exception e) {
                handler.post(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    showToast("Erreur: " + e.getMessage());
                    Log.e("Scraper", "Erreur de recherche", e);
                });
            } finally {
                executor.shutdown();
            }
        });
    }

    private double parsePrice(String priceString) {
        try {
            String numericPart = priceString.replaceAll("[^\\d.,]", "").replace(",", ".");
            return Double.parseDouble(numericPart);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}