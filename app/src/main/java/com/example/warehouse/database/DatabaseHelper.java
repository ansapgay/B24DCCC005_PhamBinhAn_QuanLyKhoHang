package com.example.warehouse.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.warehouse.model.Product;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_PRODUCTS = "products";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL)");
        db.execSQL("CREATE TABLE products ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "price INTEGER NOT NULL, "
                + "quantity INTEGER NOT NULL, "
                + "category TEXT NOT NULL, "
                + "active INTEGER NOT NULL, "
                + "description TEXT)");

        db.execSQL("INSERT INTO users (username, password) VALUES (?, ?)",
                new Object[]{"admin", "123456"});
        insertSeedProduct(db, "Chuột không dây", 150000, 20, "Điện tử",
                "Chuột không dây kết nối 2.4GHz");
        insertSeedProduct(db, "Bàn phím cơ", 450000, 3, "Điện tử",
                "Bàn phím cơ switch xanh, có đèn nền");
        insertSeedProduct(db, "Sổ tay A5", 25000, 40, "Văn phòng phẩm",
                "Sổ tay bìa cứng 200 trang");
    }

    private void insertSeedProduct(SQLiteDatabase db, String name, int price, int quantity,
                                   String category, String description) {
        db.execSQL("INSERT INTO products "
                        + "(name, price, quantity, category, active, description) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                new Object[]{name, price, quantity, category, 1, description});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version 1 is the initial schema.
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT 1 FROM users WHERE username = ? AND password = ? LIMIT 1",
                new String[]{username, password})) {
            return cursor.moveToFirst();
        }
    }

    public long insertUser(String username, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        return getWritableDatabase().insert(TABLE_USERS, null, values);
    }

    public long insertProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put("name", product.getName());
        values.put("price", product.getPrice());
        values.put("quantity", product.getQuantity());
        values.put("category", product.getCategory());
        values.put("active", product.getActive());
        values.put("description", product.getDescription());
        return getWritableDatabase().insert(TABLE_PRODUCTS, null, values);
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_PRODUCTS,
                new String[]{"id", "name", "price", "quantity", "category", "active",
                        "description"},
                null, null, null, null, "id DESC")) {
            while (cursor.moveToNext()) {
                products.add(readProduct(cursor));
            }
        }
        return products;
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_PRODUCTS,
                new String[]{"id", "name", "price", "quantity", "category", "active",
                        "description"},
                "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (cursor.moveToFirst()) {
                return readProduct(cursor);
            }
        }
        return null;
    }

    private Product readProduct(Cursor cursor) {
        return new Product(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getInt(cursor.getColumnIndexOrThrow("price")),
                cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                cursor.getString(cursor.getColumnIndexOrThrow("category")),
                cursor.getInt(cursor.getColumnIndexOrThrow("active")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")));
    }

    public int updateQuantity(int id, int quantity) {
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);
        return getWritableDatabase().update(TABLE_PRODUCTS, values, "id = ?",
                new String[]{String.valueOf(id)});
    }

    public int deleteProduct(int id) {
        return getWritableDatabase().delete(TABLE_PRODUCTS, "id = ?",
                new String[]{String.valueOf(id)});
    }

    public long getTotalValue() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT COALESCE(SUM(price * quantity), 0) FROM products", null)) {
            return cursor.moveToFirst() ? cursor.getLong(0) : 0L;
        }
    }

    public int countLowStock() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM products WHERE quantity < ?", new String[]{"5"})) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }
}
