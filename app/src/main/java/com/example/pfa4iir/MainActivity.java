package com.example.pfa4iir;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout container = findViewById(R.id.container); // Votre LinearLayout qui contient les cartes
        List<String> card_title = new ArrayList<>();
        card_title.add("Ordinateurs & PC Portables");
        card_title.add("Composants PC");
        card_title.add("Périphériques & Accessoires");
        card_title.add("Gaming & Équipements Esport");

        // Créer plusieurs cartes avec des données différentes
        for (int i = 0; i < 4; i++) {
            View cardView = getLayoutInflater().inflate(R.layout.category_card, container, false);

            TextView cardTitle = cardView.findViewById(R.id.cardTitle);
            RecyclerView recyclerView = cardView.findViewById(R.id.categoriesRecyclerView);
            TextView seeMore = cardView.findViewById(R.id.seeMoreText);

            // Configurer chaque carte différemment
            cardTitle.setText(card_title.get(i));
            seeMore.setText("Voir plus ");

            // Configurer le RecyclerView
            List<CategoryItem> items = new ArrayList<>();
            // Ajouter des éléments différents selon la carte
            if (i == 0) {
                items.add(new CategoryItem("PC de Bureau", R.drawable.desktops));
                items.add(new CategoryItem("PC Portables", R.drawable.laptops));
                items.add(new CategoryItem("PC Gaming", R.drawable.pc_gamer));
            } else if (i == 1) {
                items.add(new CategoryItem("Processeurs", R.drawable.cpu));
                items.add(new CategoryItem("Cartes Mères", R.drawable.motherboard));
                items.add(new CategoryItem("GPU", R.drawable.gpu));
                items.add(new CategoryItem("RAM", R.drawable.ram));
                items.add(new CategoryItem("SSD & HDD", R.drawable.ssd));
            } else if (i == 2) {
                items.add(new CategoryItem("Écrans ", R.drawable.ecran));
                items.add(new CategoryItem("Claviers & Souris", R.drawable.mouse));
                items.add(new CategoryItem("Casques ", R.drawable.casque));
            } else {
                items.add(new CategoryItem("Console de Jeux", R.drawable.console));
                items.add(new CategoryItem("Accessoires Gaming", R.drawable.accessoir));
                items.add(new CategoryItem("Webcams ", R.drawable.webcam));
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(new CategoryAdapter(items, this));

            container.addView(cardView);
        }

        searchEditText = findViewById(R.id.navSearchKeywords);
        searchButton = findViewById(R.id.navSearchSubmit);

        // Gestion du clic sur le bouton de recherche
        searchButton.setOnClickListener(v -> performSearch());

        // Gestion de l'action "Recherche" du clavier
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Toast.makeText(MainActivity.this, "nav_home", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_map) {
                Toast.makeText(MainActivity.this, "nav_map", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_login) {
                Toast.makeText(MainActivity.this, "nav_login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, assistant_virtuel.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String searchQuery = searchEditText.getText().toString().trim();

        if (!searchQuery.isEmpty()) {
            // Show loading indicator
            Toast.makeText(this, "Recherche en cours...", Toast.LENGTH_SHORT).show();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                try {
                    List<ProductItem> products = UltraPcScraper.scrapeProducts(searchQuery);

                    handler.post(() -> {
                        if (products == null || products.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Aucun produit trouvé", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Launch new activity with results
                        Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                        intent.putExtra("myObjectList", (Serializable) products);
                        startActivity(intent);
                    });
                } catch (Exception e) {
                    handler.post(() -> {
                        Toast.makeText(MainActivity.this, "Erreur de recherche: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("MainActivity", "Search failed", e);
                    });
                } finally {
                    executor.shutdown();
                }
            });
        } else {
            Toast.makeText(this, "Veuillez entrer un terme de recherche", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.map) {
            Toast.makeText(this, "map", Toast.LENGTH_SHORT).show();
            // openMap();
            return true;
        } else if (id == R.id.login) {
            Toast.makeText(this, "login", Toast.LENGTH_SHORT).show(); // Corrected the message
            // openLogin();
            return true;
        } else {
            Toast.makeText(this, "can't deliver", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
    }
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
//        switch (item.getItemId()) {
//            case R.id.edit:
//                editNote(info.id);
//                return true;
//            case R.id.delete:
//                deleteNote(info.id);
//                return true;
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }

    private void openMap() {
    }


}