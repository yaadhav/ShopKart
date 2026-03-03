package com.shopkart.catalog.model.enums;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public enum FashionStyle {
    MEN(1, "men", "Men"),
    WOMEN(2, "women", "Women"),
    UNISEX(3, "unisex", "Unisex");

    public final int code;
    public final String name;
    public final String displayName;

    private static final Table<Integer, String, String> TABLE;

    FashionStyle(int code, String name, String displayName) {
        this.code = code;
        this.name = name;
        this.displayName = displayName;
    }

    static {
        Table<Integer, String, String> tempTable = HashBasedTable.create();
        for(FashionStyle fs : values()) {
            tempTable.put(fs.code, fs.name, fs.displayName);
        }
        TABLE = Tables.unmodifiableTable(tempTable);
    }

    public static int getCode(String name) {
        if(!TABLE.containsColumn(name)) {
            throw new IllegalArgumentException("Invalid FashionStyle name: " + name);
        }
        return TABLE.column(name).keySet().iterator().next();
    }

    public static String getName(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid FashionStyle code: " + code);
        }
        return TABLE.row(code).keySet().iterator().next();
    }

    public static String getDisplayName(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid FashionStyle code: " + code);
        }
        return TABLE.row(code).values().iterator().next();
    }

    public static FashionStyle getFashionStyle(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid FashionStyle code: " + code);
        }
        return valueOf(TABLE.row(code).keySet().iterator().next().toUpperCase());
    }

}
