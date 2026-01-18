package com.mbaigo.datecentre.swingApp.utilities;

public class Util {
    // Dans ClientServiceImpl.java

    public static String normalizePhone(String input) {
        if (input == null) return null;
        // Enlève tous les espaces, points, tirets
        return input.replaceAll("[\\s.-]", "");
    }
}
