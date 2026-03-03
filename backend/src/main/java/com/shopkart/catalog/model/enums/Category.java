package com.shopkart.catalog.model.enums;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public enum Category {
    TSHIRT(1, "tshirt", "Tshirt"),
    SHIRT(2, "shirt", "Shirt"),
    PANTS(3, "pants", "Pants"),
    TRACK_PANTS(4, "track_pants", "Track Pants"),
    SHORTS(5, "shorts", "Shorts"),

    TOPS(6, "tops", "Tops"),
    LEGGINGS(7, "leggings", "Leggings"),
    DRESSES(8, "dresses", "Dresses");

    public final int code;
    public final String name;
    public final String displayName;

    private static final Table<Integer, String, String> TABLE;

    Category(int code, String name, String displayName) {
        this.code = code;
        this.name = name;
        this.displayName = displayName;
    }

    static {
        Table<Integer, String, String> temp = HashBasedTable.create();
        for(Category c : values()) {
            temp.put(c.code, c.name, c.displayName);
        }
        TABLE = Tables.unmodifiableTable(temp);
    }

    public static int getCode(String name) {
        if(!TABLE.containsColumn(name)) {
            throw new IllegalArgumentException("Invalid Category name: " + name);
        }
        return TABLE.column(name).keySet().iterator().next();
    }

    public static String getName(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Category code: " + code);
        }
        return TABLE.row(code).keySet().iterator().next();
    }

    public static String getDisplayName(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Category code: " + code);
        }
        return TABLE.row(code).values().iterator().next();
    }

    public static Category getCategory(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Category code: " + code);
        }
        return valueOf(TABLE.row(code).keySet().iterator().next().toUpperCase());
    }
}

