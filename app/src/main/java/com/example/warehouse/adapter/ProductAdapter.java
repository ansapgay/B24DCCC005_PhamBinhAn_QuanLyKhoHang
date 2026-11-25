package com.example.warehouse.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warehouse.R;
import com.example.warehouse.model.Product;
import com.example.warehouse.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final OnProductClickListener listener;
    private final List<Product> products = new ArrayList<>();

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(holder.itemView.getContext().getString(
                R.string.product_price, FormatUtils.formatVnd(product.getPrice())));
        holder.category.setText(product.getCategory());
        holder.quantity.setText(holder.itemView.getContext().getString(
                R.string.product_quantity, product.getQuantity()));
        boolean lowStock = product.getQuantity() < 5;
        holder.quantity.setTextColor(Color.parseColor(lowStock ? "#D32F2F" : "#37474F"));
        holder.lowStock.setVisibility(lowStock ? View.VISIBLE : View.GONE);
        holder.card.setOnClickListener(view -> listener.onProductClick(product));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final CardView card;
        final TextView name;
        final TextView price;
        final TextView category;
        final TextView quantity;
        final TextView lowStock;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.product_card);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            category = itemView.findViewById(R.id.product_category);
            quantity = itemView.findViewById(R.id.product_quantity);
            lowStock = itemView.findViewById(R.id.low_stock_badge);
        }
    }
}
