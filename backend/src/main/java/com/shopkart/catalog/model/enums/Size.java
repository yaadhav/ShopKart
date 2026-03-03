package com.shopkart.catalog.model.enums;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public enum Size {
    XS(1, "xs", "XS"),
    S(2, "s", "S"),
    M(3, "m", "M"),
    L(4, "l", "L"),
    XL(5, "xl", "XL"),
    XXL(6, "xxl", "XXL");

    public final int code;
    public final String name;
    public final String displayName;

    private static final Table<Integer, String, String> TABLE;

    Size(int code, String name, String displayName) {
        this.code = code;
        this.name = name;
        this.displayName = displayName;
    }

    static {
        Table<Integer, String, String> temp = HashBasedTable.create();
        for(Size s : values()) {
            temp.put(s.code, s.name, s.displayName);
        }
        TABLE = Tables.unmodifiableTable(temp);
    }

    public static int getCode(String name) {
        if(!TABLE.containsColumn(name)) {
            throw new IllegalArgumentException("Invalid Size name: " + name);
        }
        return TABLE.column(name).keySet().iterator().next();
    }

    public static String getName(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Size code: " + code);
        }
        return TABLE.row(code).keySet().iterator().next();
    }

    public static String getDisplayName(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Size code: " + code);
        }
        return TABLE.row(code).values().iterator().next();
    }

    public static Size getSize(int code) {
        if(!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Size code: " + code);
        }
        return valueOf(TABLE.row(code).keySet().iterator().next().toUpperCase());
    }
}

