package com.shopkart.order.dto.enums;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public enum OrderStatus {
    INITIATED(1, "initiated", "Initiated"),
    PAYMENT_PENDING(2, "payment_pending", "Payment Pending"),
    PAYMENT_FAILED(3, "payment_failed", "Payment Failed"),
    CONFIRMED(4, "confirmed", "Confirmed"),
    SHIPPED(5, "shipped", "Shipped"),
    DELIVERED(6, "delivered", "Delivered");

    public final int code;
    public final String name;
    public final String displayName;

    private static final Table<Integer, String, String> TABLE;

    OrderStatus(int code, String name, String displayName) {
        this.code = code;
        this.name = name;
        this.displayName = displayName;
    }

    static {
        Table<Integer, String, String> temp = HashBasedTable.create();
        for (OrderStatus o : values()) {
            temp.put(o.code, o.name, o.displayName);
        }
        TABLE = Tables.unmodifiableTable(temp);
    }

    public static int getCode(String name) {
        if (!TABLE.containsColumn(name)) {
            throw new IllegalArgumentException("Invalid OrderStatus name: " + name);
        }
        return TABLE.column(name).keySet().iterator().next();
    }

    public static String getName(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid OrderStatus code: " + code);
        }
        return TABLE.row(code).keySet().iterator().next();
    }

    public static String getDisplayName(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid OrderStatus code: " + code);
        }
        return TABLE.row(code).values().iterator().next();
    }

    public static OrderStatus getOrderStatus(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid OrderStatus code: " + code);
        }
        return valueOf(TABLE.row(code).keySet().iterator().next().toUpperCase());
    }
}
