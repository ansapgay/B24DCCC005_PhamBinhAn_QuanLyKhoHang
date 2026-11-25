package com.example.warehouse.utils;

import java.text.NumberFormat;
import java.util.Locale;

public final class FormatUtils {
    private FormatUtils() {
    }

    public static String formatVnd(long value) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);
        formatter.setGroupingUsed(true);
        return formatter.format(value) + " đ";
    }
}
