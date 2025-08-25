package dev.xhyrom.lanprops.common.mixin;

import dev.xhyrom.lanprops.common.accessors.CustomDedicatedServerProperties;
import dev.xhyrom.lanprops.common.accessors.CustomSettings;
import net.minecraft.server.dedicated.PropertyManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Properties;

@Mixin(PropertyManager.class)
public abstract class DedicatedServerPropertiesMixin implements CustomDedicatedServerProperties, CustomSettings {
    @Unique
    private static final HashMap<String, Class<?>> lan_properties$KEYS = new HashMap<>();

    @Shadow public abstract boolean getBooleanProperty(String key, boolean defaultValue);

    @Shadow @Final private Properties serverProperties;

    @Shadow public abstract String getStringProperty(String key, String defaultValue);

    @Shadow public abstract int getIntProperty(String key, int defaultValue);

    @Unique
    public final boolean lan_properties$hybridMode = this.getBooleanProperty("hybrid-mode", true);

    public @NotNull Properties lan_properties$properties() {
        return this.serverProperties;
    }

    public boolean lan_properties$onlineMode() {
        return this.getBooleanProperty("online-mode", true);
    }

    public boolean lan_properties$preventProxyConnections() {
        return this.getBooleanProperty("prevent-proxy-connections", false);
    }

    public boolean lan_properties$pvp() {
        return this.getBooleanProperty("pvp", true);
    }

    public boolean lan_properties$allowFlight() {
        return this.getBooleanProperty("allow-flight", false);
    }

    public String lan_properties$motd() {
        return this.getStringProperty("motd", "A Minecraft Server");
    }

    public int lan_properties$playerIdleTimeout() {
        return this.getIntProperty("player-idle-timeout", 0);
    }

    public int lan_properties$serverPort() {
        return this.getIntProperty("server-port", 25565);
    }

    public void lan_properties$serverPort(int port) {
        this.serverProperties.put("server-port", String.valueOf(port));
    }

    @Unique
    public boolean lan_properties$hybridMode() {
        return this.getBooleanProperty("hybrid-mode", this.lan_properties$hybridMode);
    }

    @Inject(method = "getIntProperty", at = @At("HEAD"))
    private void get(String key, int value, CallbackInfoReturnable<Integer> cir) {
        lan_properties$KEYS.put(key, int.class);
    }

    @Inject(method = "getStringProperty", at = @At("HEAD"))
    private void get(String key, String value, CallbackInfoReturnable<Integer> cir) {
        lan_properties$KEYS.put(key, String.class);
    }

    @Inject(method = "getBooleanProperty", at = @At("HEAD"))
    private void get(String key, boolean value, CallbackInfoReturnable<Integer> cir) {
        lan_properties$KEYS.put(key, boolean.class);
    }

    @Inject(method = "getLongProperty", at = @At("HEAD"))
    private void get(String key, long value, CallbackInfoReturnable<Integer> cir) {
        lan_properties$KEYS.put(key, long.class);
    }

    @Override
    public @NotNull HashMap<String, Class<?>> lan_properties$keys() {
        return lan_properties$KEYS;
    }
}
