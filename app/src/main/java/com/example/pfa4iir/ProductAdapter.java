// ProductAdapter.java
package com.example.pfa4iir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<ProductItem> productList;
    private Context context;

    public ProductAdapter(List<ProductItem> productList, Context context) {
        this.productList = productList != null ? productList : new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductItem product = productList.get(position);

        // Load image with Glide
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.productImage);

        holder.productTitle.setText(product.getName());
        holder.productPrice.setText(product.getPrice());

        // Disponibilité
        if (product.isAvailable()) {
            holder.productAvailability.setVisibility(View.VISIBLE);
            holder.productAvailability1.setVisibility(View.GONE);
        } else {
            holder.productAvailability.setVisibility(View.GONE);
            holder.productAvailability1.setVisibility(View.VISIBLE);
        }

        // Promo / Réduction
        boolean hasPromo = product.getPromoText() != null && !product.getPromoText().isEmpty();
        boolean hasDiscount = product.getDiscountText() != null && !product.getDiscountText().isEmpty();

        if (hasPromo || hasDiscount) {
            holder.promoText.setText(product.getPromoText());
            holder.discountText.setText(product.getDiscountText());
            holder.promoText.setVisibility(hasPromo ? View.VISIBLE : View.GONE);
            holder.discountText.setVisibility(hasDiscount ? View.VISIBLE : View.GONE);
            holder.itemView.findViewById(R.id.promoContainer).setVisibility(View.VISIBLE);
        } else {
            holder.itemView.findViewById(R.id.promoContainer).setVisibility(View.GONE);
        }

        // Boutons (fonctionnalité à implémenter plus tard)
        holder.addToCartButton.setOnClickListener(v -> {
            Toast.makeText(context, "Ajouté au panier : " + product.getName(), Toast.LENGTH_SHORT).show();
        });

        holder.quickViewButton.setOnClickListener(v -> {
            Toast.makeText(context, "Aperçu rapide : " + product.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProducts(List<ProductItem> newProducts) {
        this.productList.clear();
        this.productList.addAll(newProducts != null ? newProducts : new ArrayList<>());
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle, productPrice;
        TextView promoText, discountText, productAvailability, productAvailability1;
        Button addToCartButton, quickViewButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productPrice = itemView.findViewById(R.id.productPrice);
            productAvailability = itemView.findViewById(R.id.productAvailability);
            productAvailability1 = itemView.findViewById(R.id.productAvailability1);
            promoText = itemView.findViewById(R.id.promoText);
            discountText = itemView.findViewById(R.id.discountText);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            quickViewButton = itemView.findViewById(R.id.quickViewButton);
        }

    }
}