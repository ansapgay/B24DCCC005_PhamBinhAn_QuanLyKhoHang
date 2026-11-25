package com.example.warehouse.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warehouse.R;
import com.example.warehouse.adapter.ProductAdapter;
import com.example.warehouse.database.DatabaseHelper;
import com.example.warehouse.model.Product;
import com.example.warehouse.utils.FormatUtils;

import java.util.List;

public class MainActivity extends Activity {
    private DatabaseHelper databaseHelper;
    private ProductAdapter productAdapter;
    private TextView totalProducts;
    private TextView totalValue;
    private TextView lowStockCount;
    private TextView emptyState;
    private RecyclerView productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        totalProducts = findViewById(R.id.total_products);
        totalValue = findViewById(R.id.total_value);
        lowStockCount = findViewById(R.id.low_stock_count);
        emptyState = findViewById(R.id.empty_state);
        productList = findViewById(R.id.product_list);
        productAdapter = new ProductAdapter(product ->
                openProductDetails(product.getId()));
        productList.setLayoutManager(new LinearLayoutManager(this));
        productList.setAdapter(productAdapter);

        Button addProductButton = findViewById(R.id.add_product_button);
        addProductButton.setOnClickListener(view ->
                startActivity(new Intent(this, AddProductActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInventory();
    }

    private void loadInventory() {
        List<Product> products = databaseHelper.getAllProducts();
        productAdapter.setProducts(products);
        totalProducts.setText(String.valueOf(products.size()));
        totalValue.setText(FormatUtils.formatVnd(databaseHelper.getTotalValue()));
        lowStockCount.setText(String.valueOf(databaseHelper.countLowStock()));
        boolean isEmpty = products.isEmpty();
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        productList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void openProductDetails(int productId) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, productId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
