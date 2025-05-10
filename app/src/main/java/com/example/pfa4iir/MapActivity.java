package com.example.pfa4iir;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        gMap = googleMap;
        LatLng ultraPC = new LatLng(33.5883, -7.6282);  // Coord approximative
        gMap.addMarker(new MarkerOptions().position(ultraPC).title("UltraPC Casablanca"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ultraPC, 14));


        // Marqueur SetupGame Casablanca
        LatLng setupGame = new LatLng(33.5905, -7.6412);  // Coord approximative
        gMap.addMarker(new MarkerOptions().position(setupGame).title("SetupGame Casablanca"));

        // (Facultatif) Marqueur Micromagma Casablanca
        LatLng micromagma = new LatLng(33.5875, -7.6246);
        gMap.addMarker(new MarkerOptions().position(micromagma).title("Micromagma Casablanca"));

        // (Facultatif) Marqueur MarokIT Casablanca
        LatLng marokit = new LatLng(33.5860, -7.6185);
        gMap.addMarker(new MarkerOptions().position(marokit).title("MarokIT Casablanca"));
    }
}