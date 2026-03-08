package com.shopkart.order.dto.enums;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public enum PaymentMode {
    ONLINE(1, "online", "Online"),
    PAY_ON_DELIVERY(2, "pay_on_delivery", "Pay on Delivery");

    public final int code;
    public final String name;
    public final String displayName;

    private static final Table<Integer, String, String> TABLE;

    PaymentMode(int code, String name, String displayName) {
        this.code = code;
        this.name = name;
        this.displayName = displayName;
    }

    static {
        Table<Integer, String, String> temp = HashBasedTable.create();
        for (PaymentMode p : values()) {
            temp.put(p.code, p.name, p.displayName);
        }
        TABLE = Tables.unmodifiableTable(temp);
    }

    public static int getCode(String name) {
        if (!TABLE.containsColumn(name)) {
            throw new IllegalArgumentException("Invalid PaymentMode name: " + name);
        }
        return TABLE.column(name).keySet().iterator().next();
    }

    public static String getName(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid PaymentMode code: " + code);
        }
        return TABLE.row(code).keySet().iterator().next();
    }

    public static String getDisplayName(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid PaymentMode code: " + code);
        }
        return TABLE.row(code).values().iterator().next();
    }

    public static PaymentMode getPaymentMode(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid PaymentMode code: " + code);
        }
        return valueOf(TABLE.row(code).keySet().iterator().next().toUpperCase());
    }
}
