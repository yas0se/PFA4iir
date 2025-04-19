package com.example.pfa4iir;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_results);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialiser le RecyclerView
        RecyclerView recyclerView = findViewById(R.id.productsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 colonnes

        // Créer la liste des produits
        List<ProductItem> productList = new ArrayList<>();
        productList.add(new ProductItem(
                "1",
                "Kingston SODIMM 32GB 5600 DDR5 CL40",
                R.drawable.ram_example,
                "1 099,00 MAD",
                "1 199,00 MAD",
                true,
                "Promo !",
                "-100,00 MAD",
                true,
                "En stock"
        ));

        productList.add(new ProductItem(
                "2",
                "Disque Dur SSD 1TB",
                R.drawable.ssd_example,
                "899,00 MAD",
                "999,00 MAD",
                false,
                null,
                null,
                false,
                "Rupture de stock"
        ));

        // Définir l'adapteur
        ProductAdapter adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);


    }
}