package com.shopkart.common.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class CurrencyUtil {

    private static final NumberFormat INR_FORMAT = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public static String formatWithINR(BigDecimal amount) {
        if (amount == null) return null;
        return INR_FORMAT.format(amount);
    }
}
