package com.ibanity.apis.client.utils;

import org.apache.commons.lang3.Strings;

public class StringUtils {

    public static String removeEnd(String str, String remove) {
        return Strings.CS.removeEnd(str, remove);
    }

    public static boolean isNotBlank(String cs) {
        return !isBlank(cs);
    }

    public static boolean isBlank(String cs) {
        return cs == null || cs.isBlank();
    }
}
