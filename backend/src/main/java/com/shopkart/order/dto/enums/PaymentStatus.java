package com.shopkart.order.dto.enums;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public enum PaymentStatus {
    YET_TO_BE_PAID(1, "yet_to_be_paid", "Yet to be Paid"),
    INITIATED(2, "initiated", "Initiated"),
    PAID(3, "paid", "Paid"),
    FAILED(4, "failed", "Failed");

    public final int code;
    public final String name;
    public final String displayName;

    private static final Table<Integer, String, String> TABLE;

    PaymentStatus(int code, String name, String displayName) {
        this.code = code;
        this.name = name;
        this.displayName = displayName;
    }

    static {
        Table<Integer, String, String> temp = HashBasedTable.create();
        for (PaymentStatus p : values()) {
            temp.put(p.code, p.name, p.displayName);
        }
        TABLE = Tables.unmodifiableTable(temp);
    }

    public static int getCode(String name) {
        if (!TABLE.containsColumn(name)) {
            throw new IllegalArgumentException("Invalid PaymentStatus name: " + name);
        }
        return TABLE.column(name).keySet().iterator().next();
    }

    public static String getName(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid PaymentStatus code: " + code);
        }
        return TABLE.row(code).keySet().iterator().next();
    }

    public static String getDisplayName(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid PaymentStatus code: " + code);
        }
        return TABLE.row(code).values().iterator().next();
    }

    public static PaymentStatus getPaymentStatus(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid PaymentStatus code: " + code);
        }
        return valueOf(TABLE.row(code).keySet().iterator().next().toUpperCase());
    }
}
