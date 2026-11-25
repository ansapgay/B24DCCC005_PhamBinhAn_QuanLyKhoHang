package com.example.warehouse.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.warehouse.R;
import com.example.warehouse.database.DatabaseHelper;
import com.example.warehouse.model.Product;

public class AddProductActivity extends Activity {
    private DatabaseHelper databaseHelper;
    private EditText nameInput;
    private EditText priceInput;
    private EditText quantityInput;
    private EditText descriptionInput;
    private Spinner categorySpinner;
    private Switch activeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        databaseHelper = new DatabaseHelper(this);
        nameInput = findViewById(R.id.name_input);
        priceInput = findViewById(R.id.price_input);
        quantityInput = findViewById(R.id.quantity_input);
        descriptionInput = findViewById(R.id.description_input);
        categorySpinner = findViewById(R.id.category_spinner);
        activeSwitch = findViewById(R.id.active_switch);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.product_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        Button saveButton = findViewById(R.id.save_product_button);
        saveButton.setOnClickListener(view -> saveProduct());
    }

    private void saveProduct() {
        String name = nameInput.getText().toString().trim();
        String priceText = priceInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError(getString(R.string.required_field));
            return;
        }
        if (priceText.isEmpty()) {
            priceInput.setError(getString(R.string.required_field));
            return;
        }
        if (quantityText.isEmpty()) {
            quantityInput.setError(getString(R.string.required_field));
            return;
        }

        final int price;
        final int quantity;
        try {
            price = Integer.parseInt(priceText);
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException exception) {
            priceInput.setError(getString(R.string.invalid_number));
            return;
        }
        if (price <= 0) {
            priceInput.setError(getString(R.string.price_must_be_positive));
            return;
        }
        if (quantity < 0) {
            quantityInput.setError(getString(R.string.quantity_must_not_be_negative));
            return;
        }

        Product product = new Product(name, price, quantity,
                categorySpinner.getSelectedItem().toString(),
                activeSwitch.isChecked() ? 1 : 0,
                descriptionInput.getText().toString().trim());
        long insertedId = databaseHelper.insertProduct(product);
        if (insertedId == -1L) {
            Toast.makeText(this, R.string.save_product_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, R.string.save_product_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
