package fr.zankia.stock.dao;

import android.provider.BaseColumns;

public final class StockContract {
    private StockContract() {}

    public static class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "Category";
        public static final String COLUMN_NAME_NAME = "name";
    }

    public static class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "Product";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CAT = "category";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
    }
}
