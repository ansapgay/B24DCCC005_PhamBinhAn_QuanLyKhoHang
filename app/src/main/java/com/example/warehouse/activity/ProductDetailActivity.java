package com.example.warehouse.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.warehouse.R;
import com.example.warehouse.database.DatabaseHelper;
import com.example.warehouse.model.Product;
import com.example.warehouse.utils.FormatUtils;

public class ProductDetailActivity extends Activity {
    public static final String EXTRA_PRODUCT_ID = "product_id";

    private DatabaseHelper databaseHelper;
    private int productId;
    private Product product;
    private TextView name;
    private TextView category;
    private TextView price;
    private TextView quantity;
    private TextView lowStock;
    private TextView stockValue;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        databaseHelper = new DatabaseHelper(this);
        productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        name = findViewById(R.id.detail_name);
        category = findViewById(R.id.detail_category);
        price = findViewById(R.id.detail_price);
        quantity = findViewById(R.id.detail_quantity);
        lowStock = findViewById(R.id.detail_low_stock);
        stockValue = findViewById(R.id.detail_stock_value);
        description = findViewById(R.id.detail_description);

        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        findViewById(R.id.receive_button).setOnClickListener(view -> changeQuantity(1));
        findViewById(R.id.sell_button).setOnClickListener(view -> changeQuantity(-1));
        findViewById(R.id.return_button).setOnClickListener(view -> finish());
        findViewById(R.id.delete_button).setOnClickListener(view -> deleteProduct());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProduct();
    }

    private void loadProduct() {
        if (productId <= 0) {
            Toast.makeText(this, R.string.product_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        product = databaseHelper.getProductById(productId);
        if (product == null) {
            Toast.makeText(this, R.string.product_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        name.setText(product.getName());
        category.setText(product.getCategory());
        price.setText(FormatUtils.formatVnd(product.getPrice()));
        quantity.setText(getString(R.string.stock_quantity, product.getQuantity()));
        lowStock.setVisibility(product.getQuantity() < 5 ? View.VISIBLE : View.GONE);
        stockValue.setText(FormatUtils.formatVnd(
                (long) product.getPrice() * product.getQuantity()));
        String productDescription = product.getDescription();
        description.setText(productDescription == null || productDescription.trim().isEmpty()
                ? getString(R.string.no_description) : productDescription);
    }

    private void changeQuantity(int change) {
        if (product == null) {
            return;
        }
        if (change < 0 && product.getQuantity() == 0) {
            Toast.makeText(this, R.string.out_of_stock, Toast.LENGTH_SHORT).show();
            return;
        }
        if (change > 0 && product.getQuantity() == Integer.MAX_VALUE) {
            Toast.makeText(this, R.string.quantity_limit_reached, Toast.LENGTH_SHORT).show();
            return;
        }

        int newQuantity = product.getQuantity() + change;
        if (databaseHelper.updateQuantity(productId, newQuantity) > 0) {
            product.setQuantity(newQuantity);
            loadProduct();
        } else {
            Toast.makeText(this, R.string.product_not_found, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void deleteProduct() {
        if (databaseHelper.deleteProduct(productId) > 0) {
            finish();
        } else {
            Toast.makeText(this, R.string.product_not_found, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
