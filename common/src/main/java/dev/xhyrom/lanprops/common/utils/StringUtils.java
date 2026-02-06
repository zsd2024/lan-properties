package dev.xhyrom.lanprops.common.utils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class StringUtils {
    private static final Map<String, String> PROPERTY_TRANSLATIONS = new HashMap<>();
    
    static {
        // 服务器属性翻译
        PROPERTY_TRANSLATIONS.put("online-mode", "在线模式");
        PROPERTY_TRANSLATIONS.put("prevent-proxy-connections", "防止代理连接");
        PROPERTY_TRANSLATIONS.put("pvp", "PvP");
        PROPERTY_TRANSLATIONS.put("allow-flight", "允许飞行");
        PROPERTY_TRANSLATIONS.put("motd", "服务器描述");
        PROPERTY_TRANSLATIONS.put("player-idle-timeout", "玩家闲置超时");
        PROPERTY_TRANSLATIONS.put("server-port", "服务器端口");
        PROPERTY_TRANSLATIONS.put("hybrid-mode", "混合模式");
        PROPERTY_TRANSLATIONS.put("spawn-animals", "生成动物");
        PROPERTY_TRANSLATIONS.put("spawn-npcs", "生成NPC");
        PROPERTY_TRANSLATIONS.put("resource-pack", "资源包");
        PROPERTY_TRANSLATIONS.put("difficulty", "难度");
        PROPERTY_TRANSLATIONS.put("max-build-height", "最大建筑高度");
        PROPERTY_TRANSLATIONS.put("announce-player-achievements", "公告玩家成就");
        PROPERTY_TRANSLATIONS.put("resource-pack-hash", "资源包哈希");
        PROPERTY_TRANSLATIONS.put("resource-pack-sha1", "资源包SHA1");
        PROPERTY_TRANSLATIONS.put("hardcore", "极限模式");
        PROPERTY_TRANSLATIONS.put("allow-nether", "允许下界");
        PROPERTY_TRANSLATIONS.put("spawn-monsters", "生成怪物");
        PROPERTY_TRANSLATIONS.put("snooper-enabled", "启用数据收集");
        PROPERTY_TRANSLATIONS.put("use-native-transport", "使用本地传输");
        PROPERTY_TRANSLATIONS.put("enable-command-block", "启用命令方块");
        PROPERTY_TRANSLATIONS.put("spawn-protection", "出生点保护");
        PROPERTY_TRANSLATIONS.put("op-permission-level", "OP权限等级");
        PROPERTY_TRANSLATIONS.put("broadcast-rcon-to-ops", "向OP广播RCON");
        PROPERTY_TRANSLATIONS.put("broadcast-console-to-ops", "向OP广播控制台");
        PROPERTY_TRANSLATIONS.put("max-world-size", "最大世界大小");
        PROPERTY_TRANSLATIONS.put("network-compression-threshold", "网络压缩阈值");
        PROPERTY_TRANSLATIONS.put("enforce-whitelist", "强制白名单");
    }
    
    public static String kebabCaseToTitleCase(final @NotNull String kebab) {
        // 首先检查是否有翻译
        if (PROPERTY_TRANSLATIONS.containsKey(kebab)) {
            return PROPERTY_TRANSLATIONS.get(kebab);
        }
        
        // 如果没有翻译，使用原来的逻辑但输出中文格式
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
