package com.shopkart.user.dto.enums;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public enum Role {
    USER(1, "user", "User"),
    ADMIN(2, "admin", "Admin"),
    OWNER(3, "owner", "Owner"),
    SUPER_ADMIN(4, "super_admin", "Super Admin"),
    ORDER_ADMIN(5, "order_admin", "Order Admin"),
    PRODUCT_ADMIN(6, "product_admin", "Product Admin");

    public final int code;
    public final String name;
    public final String displayName;

    private static final Table<Integer, String, String> TABLE;

    Role(int code, String name, String displayName) {
        this.code = code;
        this.name = name;
        this.displayName = displayName;
    }

    static {
        Table<Integer, String, String> temp = HashBasedTable.create();
        for (Role r : values()) {
            temp.put(r.code, r.name, r.displayName);
        }
        TABLE = Tables.unmodifiableTable(temp);
    }

    public static int getCode(String name) {
        if (!TABLE.containsColumn(name)) {
            throw new IllegalArgumentException("Invalid Role name: " + name);
        }
        return TABLE.column(name).keySet().iterator().next();
    }

    public static String getName(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Role code: " + code);
        }
        return TABLE.row(code).keySet().iterator().next();
    }

    public static String getDisplayName(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Role code: " + code);
        }
        return TABLE.row(code).values().iterator().next();
    }

    public static Role getRole(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid Role code: " + code);
        }
        return valueOf(TABLE.row(code).keySet().iterator().next().toUpperCase());
    }
}
