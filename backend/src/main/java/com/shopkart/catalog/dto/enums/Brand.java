package com.shopkart.catalog.dto.enums;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public enum Brand {
    ZENFIT(1, "zenfit", "Zenfit"),
    URBANEDGE(2, "urbanedge", "Urbanedge"),
    ATHLEVO(3, "athlevo", "Athlevo");

    public final int code;
    public final String name;
    public final String displayName;

    private static final Table<Integer, String, String> TABLE;

    Brand(int code, String name, String displayName) {
        this.code = code;
        this.name = name;
        this.displayName = displayName;
    }

    static {
        Table<Integer, String, String> temp = HashBasedTable.create();
        for(Brand b : values()) {
            temp.put(b.code, b.name, b.displayName);
        }
        TABLE = Tables.unmodifiableTable(temp);
    }

    public static int getCode(String name) {
        if(!TABLE.containsColumn(name)) {
            throw new IllegalArgumentException("Invalid Brand name: " + name);
        }
        return TABLE.column(name).keySet().iterator().next();
    }

    public static String getName(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Brand code: " + code);
        }
        return TABLE.row(code).keySet().iterator().next();
    }

    public static String getDisplayName(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Brand code: " + code);
        }
        return TABLE.row(code).values().iterator().next();
    }

    public static Brand getBrand(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Brand code: " + code);
        }
        return valueOf(TABLE.row(code).keySet().iterator().next().toUpperCase());
    }
}

