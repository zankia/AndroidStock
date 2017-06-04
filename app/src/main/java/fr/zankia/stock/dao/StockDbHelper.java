package fr.zankia.stock.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StockDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Stock.db";

    private static final String SQL_CREATE_CATEGORIES =
            "CREATE TABLE IF NOT EXISTS " + StockContract.CategoryEntry.TABLE_NAME + " (" +
            StockContract.CategoryEntry._ID + " INTEGER PRIMARY KEY," +
            StockContract.CategoryEntry.COLUMN_NAME_NAME + " TEXT)";
    private static final  String SQL_CREATE_PRODUCTS =
            "CREATE TABLE IF NOT EXISTS " + StockContract.ProductEntry.TABLE_NAME + " (" +
            StockContract.ProductEntry._ID + " INTEGER PRIMARY KEY," +
            StockContract.ProductEntry.COLUMN_NAME_NAME + " TEXT," +
            StockContract.ProductEntry.COLUMN_NAME_CAT + " TEXT," +
            StockContract.ProductEntry.COLUMN_NAME_QUANTITY + " INTEGER)";

    private static final String SQL_DELETE_CATEGORIES =
            "DROP TABLE IF EXISTS " + StockContract.CategoryEntry.TABLE_NAME;
    private static final String SQL_DELETE_PRODUCTS =
            "DROP TABLE IF EXISTS " + StockContract.ProductEntry.TABLE_NAME;


    public StockDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CATEGORIES);
        db.execSQL(SQL_CREATE_PRODUCTS);
        Log.d("DB", "create");
        Log.d("DB", SQL_CREATE_CATEGORIES);
        Log.d("DB", SQL_CREATE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DB", "upgrade");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.d("DB", "open");
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_CATEGORIES);
        db.execSQL(SQL_DELETE_PRODUCTS);
        Log.d("DB", "delete");
    }
}
