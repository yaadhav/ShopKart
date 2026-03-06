package com.shopkart.catalog.dto.enums;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public enum Occasion {
    CASUAL(1, "casual", "Casual"),
    ACTIVE(2, "active", "Active"),
    FORMAL(3, "formal", "Formal");

    public final int code;
    public final String name;
    public final String displayName;

    private static final Table<Integer, String, String> TABLE;

    Occasion(int code, String name, String displayName) {
        this.code = code;
        this.name = name;
        this.displayName = displayName;
    }

    static {
        Table<Integer, String, String> temp = HashBasedTable.create();
        for(Occasion o : values()) {
            temp.put(o.code, o.name, o.displayName);
        }
        TABLE = Tables.unmodifiableTable(temp);
    }

    public static int getCode(String name) {
        if(!TABLE.containsColumn(name)) {
            throw new IllegalArgumentException("Invalid Occasion name: " + name);
        }
        return TABLE.column(name).keySet().iterator().next();
    }

    public static String getName(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Occasion code: " + code);
        }
        return TABLE.row(code).keySet().iterator().next();
    }

    public static String getDisplayName(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Occasion code: " + code);
        }
        return TABLE.row(code).values().iterator().next();
    }

    public static Occasion getOccasion(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Occasion code: " + code);
        }
        return valueOf(TABLE.row(code).keySet().iterator().next().toUpperCase());
    }
}

