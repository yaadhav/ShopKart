package com.shopkart.payment.dto.enums;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public enum PaymentMethod {
    UPI(1, "upi", "UPI"),
    CARD(2, "card", "Card"),
    NET_BANKING(3, "net_banking", "Net Banking");

    public final int code;
    public final String name;
    public final String displayName;

    private static final Table<Integer, String, String> TABLE;

    PaymentMethod(int code, String name, String displayName) {
        this.code = code;
        this.name = name;
        this.displayName = displayName;
    }

    static {
        Table<Integer, String, String> temp = HashBasedTable.create();
        for (PaymentMethod p : values()) {
            temp.put(p.code, p.name, p.displayName);
        }
        TABLE = Tables.unmodifiableTable(temp);
    }

    public static int getCode(String name) {
        if (!TABLE.containsColumn(name)) {
            throw new IllegalArgumentException("Invalid PaymentMethod name: " + name);
        }
        return TABLE.column(name).keySet().iterator().next();
    }

    public static String getName(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid PaymentMethod code: " + code);
        }
        return TABLE.row(code).keySet().iterator().next();
    }

    public static String getDisplayName(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid PaymentMethod code: " + code);
        }
        return TABLE.row(code).values().iterator().next();
    }

    public static PaymentMethod getPaymentMethod(int code) {
        if (!TABLE.containsRow(code)) {
            throw new IllegalArgumentException("Invalid PaymentMethod code: " + code);
        }
        return valueOf(TABLE.row(code).keySet().iterator().next().toUpperCase());
    }
}
