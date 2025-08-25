package dev.xhyrom.lanprops.common.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import dev.xhyrom.lanprops.common.accessors.CustomDedicatedServerProperties;
import dev.xhyrom.lanprops.common.accessors.CustomIntegratedServer;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin extends MinecraftServer implements CustomIntegratedServer {
    public IntegratedServerMixin(File p_i47054_1, Proxy p_i47054_2, DataFixer p_i47054_3, YggdrasilAuthenticationService p_i47054_4, MinecraftSessionService p_i47054_5, GameProfileRepository p_i47054_6, PlayerProfileCache p_i47054_7) {
        super(p_i47054_1, p_i47054_2, p_i47054_3, p_i47054_4, p_i47054_5, p_i47054_6, p_i47054_7);
    }

    @Unique
    public Path lan_properties$propertiesPath;
    @Unique
    public PropertyManager lan_properties$settings;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(Minecraft p_i46523_1, String p_i46523_2, String p_i46523_3, WorldSettings p_i46523_4, YggdrasilAuthenticationService p_i46523_5, MinecraftSessionService p_i46523_6, GameProfileRepository p_i46523_7, PlayerProfileCache p_i46523_8, CallbackInfo ci) {
        this.lan_properties$propertiesPath = this.getActiveAnvilConverter().getFile(this.getFolderName(), "server.properties").toPath();
        this.lan_properties$settings = new PropertyManager(this.lan_properties$propertiesPath.toFile());
        this.lan_properties$settings.saveProperties();
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void onInitServer(CallbackInfoReturnable<Boolean> cir) {
        this.lan_properties$updateSettings();
    }

    @Unique
    private void lan_properties$updateSettings() {
        CustomDedicatedServerProperties dedicatedServerProperties = (CustomDedicatedServerProperties) this.lan_properties$settings;
        this.setOnlineMode(dedicatedServerProperties.lan_properties$onlineMode());
        this.setPreventProxyConnections(dedicatedServerProperties.lan_properties$preventProxyConnections());
        this.setAllowPvp(dedicatedServerProperties.lan_properties$pvp());
        this.setAllowFlight(dedicatedServerProperties.lan_properties$allowFlight());
        this.setMOTD(dedicatedServerProperties.lan_properties$motd());
        super.setPlayerIdleTimeout(dedicatedServerProperties.lan_properties$playerIdleTimeout());
    }

    @Inject(method = "shareToLAN", at = @At("HEAD"))
    public void onPublishServer(GameType type, boolean allowCheats, CallbackInfoReturnable<String> cir) {
        this.lan_properties$customProperties().lan_properties$serverPort(((CustomDedicatedServerProperties) this.lan_properties$settings).lan_properties$serverPort());
        this.lan_properties$updateSettings();
    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    public void onStopServer(CallbackInfo ci) {
        this.lan_properties$settings.saveProperties();
    }

    public EnumDifficulty getDifficulty() {
        return EnumDifficulty.byId(this.lan_properties$settings.getIntProperty("difficulty", EnumDifficulty.NORMAL.getId()));
    }

    public boolean isHardcore() {
        return this.lan_properties$settings.getBooleanProperty("hardcore", false);
    }

    public boolean getAllowNether() {
        return this.lan_properties$settings.getBooleanProperty("allow-nether", true);
    }

    public boolean allowSpawnMonsters() {
        return this.lan_properties$settings.getBooleanProperty("spawn-monsters", true);
    }

    public boolean isSnooperEnabled() {
        return this.lan_properties$settings.getBooleanProperty("snooper-enabled", true);
    }

    public boolean shouldUseNativeTransport() {
        return this.lan_properties$settings.getBooleanProperty("use-native-transport", true);
    }

    public boolean isCommandBlockEnabled() {
        return this.lan_properties$settings.getBooleanProperty("enable-command-block", false);
    }

    public int getSpawnProtectionSize() {
        return this.lan_properties$settings.getIntProperty("spawn-protection", super.getSpawnProtectionSize());
    }

    public int getOpPermissionLevel() {
        return this.lan_properties$settings.getIntProperty("op-permission-level", 4);
    }

    public void setPlayerIdleTimeout(int idleTimeout) {
        super.setPlayerIdleTimeout(idleTimeout);
        this.lan_properties$settings.setProperty("player-idle-timeout", idleTimeout);
        this.lan_properties$settings.saveProperties();
    }

    public boolean shouldBroadcastRconToOps() {
        return this.lan_properties$settings.getBooleanProperty("broadcast-rcon-to-ops", true);
    }

    public boolean shouldBroadcastConsoleToOps() {
        return this.lan_properties$settings.getBooleanProperty("broadcast-console-to-ops", true);
    }

    public int getMaxWorldSize() {
        int i = this.lan_properties$settings.getIntProperty("max-world-size", super.getMaxWorldSize());
        if (i < 1) {
            i = 1;
        } else if (i > super.getMaxWorldSize()) {
            i = super.getMaxWorldSize();
        }

        return i;
    }

    public int getNetworkCompressionThreshold() {
        return this.lan_properties$settings.getIntProperty("network-compression-threshold", super.getNetworkCompressionThreshold());
    }
}
