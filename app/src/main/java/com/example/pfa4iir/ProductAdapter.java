package com.example.pfa4iir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
//import com.bumptech.glide.Glide;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductItem> productList;

    public ProductAdapter(Context context, List<ProductItem> productList) {
        this.context = context;
        this.productList = productList;
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

        // Charger l'image
        Glide.with(context)
                .load(product.getImageResId())
                .into(holder.productImage);

        holder.productTitle.setText(product.getTitle());
        holder.productPrice.setText(product.getCurrentPrice());
        holder.productOldPrice.setText(product.getOldPrice());

        // Gestion promo
        if (product.isPromo()) {
            holder.promoContainer.setVisibility(View.VISIBLE);
            holder.promoText.setText(product.getPromoText());
            holder.discountText.setText(product.getDiscountText());
        } else {
            holder.promoContainer.setVisibility(View.GONE);
        }

        // Gestion disponibilité
        holder.availability.setText(product.getAvailabilityText());
        int colorRes = product.isAvailable() ? R.color.green : R.color.red;
        holder.availability.setTextColor(ContextCompat.getColor(context, colorRes));

        // Clic sur le bouton
        holder.addToCartButton.setOnClickListener(v -> {
            // Ajouter au panier
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle, productPrice, productOldPrice;
        LinearLayout promoContainer;
        TextView promoText, discountText, availability;
        Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productPrice = itemView.findViewById(R.id.productPrice);
            productOldPrice = itemView.findViewById(R.id.productOldPrice);
            promoContainer = itemView.findViewById(R.id.promoContainer);
            promoText = itemView.findViewById(R.id.promoText);
            discountText = itemView.findViewById(R.id.discountText);
            availability = itemView.findViewById(R.id.productAvailability);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}
