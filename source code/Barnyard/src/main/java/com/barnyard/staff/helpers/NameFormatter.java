package com.barnyard.staff.helpers;

public class NameFormatter {
    public static String format(String s) {
        s = s.trim();
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
