package com.soleil.mobileguard.utils;

public class JsonSpecialSymbolUtils {
    public static String changeStr(String json) {
        json = json.replaceAll(",", "，");
        json = json.replaceAll(":", "：");
        json = json.replaceAll("\\[", "【");
        json = json.replaceAll("\\]", "】");
        json = json.replaceAll("\\{", "<");
        json = json.replaceAll("\\}", ">");
        json = json.replaceAll("\"", "“");
        return json.toString();
    }
}
