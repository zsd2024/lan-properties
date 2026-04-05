package dev.xhyrom.lanprops.common.utils;

import org.jetbrains.annotations.NotNull;

public class StringUtils {
    public static String kebabCaseToTitleCase(final @NotNull String kebab) {
        // 由于common模块不能直接访问Minecraft客户端API，使用原始的kebab-case转标题格式
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : kebab.toCharArray()) {
            if (c == '-') {
                result.append(' ');
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }
}
