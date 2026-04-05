package dev.xhyrom.lanprops.common.utils;

import net.minecraft.client.resources.I18n;
import org.jetbrains.annotations.NotNull;

public class StringUtils {
    public static String kebabCaseToTitleCase(final @NotNull String kebab) {
        // 使用I18n系统进行翻译
        String key = "lan_properties.property." + kebab;
        if (I18n.hasKey(key)) {
            return I18n.format(key);
        }
        
        // 如果没有翻译，使用默认的kebab-case转标题格式
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
