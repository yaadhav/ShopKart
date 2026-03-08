package com.shopkart.user.dto.enums;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public enum Gender {
    MALE( 1, "male", "Male"),
    FEMALE( 2, "female", "Female"),
    OTHER( 3, "other", "Other");

    public final int code;
    public final String name;
    public final String displayName;

    private static final Table<Integer, String, String> TABLE;

    Gender(int code, String name, String displayName) {
        this.code = code;
        this.name = name;
        this.displayName = displayName;
    }

    static {
        Table<Integer, String, String> temp = HashBasedTable.create();
        for (Gender g : values()) {
            temp.put(g.code, g.name, g.displayName);
        }
        TABLE = Tables.unmodifiableTable(temp);
    }

    public static String getName(Integer code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Gender code: " + code);
        }
        return TABLE.row(code).keySet().iterator().next();
    }

    public static String getDisplayName(Integer code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Gender code: " + code);
        }
        return TABLE.row(code).values().iterator().next();
    }
}
