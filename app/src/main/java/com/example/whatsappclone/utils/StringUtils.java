package com.example.whatsappclone.utils;

public class StringUtils {

    public static String normalizePhone(String phone) {
        return phone.replace(" ", "")
                .replace("-", "")
                .replace("(", "")
                .replace(")", "");
    }

}
