package dev.xhyrom.lanprops.common.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import dev.xhyrom.lanprops.common.accessors.CustomDedicatedServerProperties;
import dev.xhyrom.lanprops.common.accessors.CustomIntegratedServer;
import dev.xhyrom.lanprops.common.manager.PropertyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import java.util.regex.Pattern;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin extends MinecraftServer implements CustomIntegratedServer {
    @Shadow public abstract File getDataDirectory();

    @Unique
    private static final Pattern RESOURCE_PACK_SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
    @Shadow @Final private static Logger LOGGER;

    public IntegratedServerMixin(File p_i47054_1, Proxy p_i47054_2, DataFixer p_i47054_3, YggdrasilAuthenticationService p_i47054_4, MinecraftSessionService p_i47054_5, GameProfileRepository p_i47054_6, PlayerProfileCache p_i47054_7) {
        super(p_i47054_1, p_i47054_2, p_i47054_3, p_i47054_4, p_i47054_5, p_i47054_6, p_i47054_7);
    }

    @Unique
    public Path lan_properties$propertiesPath;
    @Unique
    public PropertyManager lan_properties$settings;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(Minecraft p_i46523_1, String p_i46523_2, String p_i46523_3, WorldSettings p_i46523_4, YggdrasilAuthenticationService p_i46523_5, MinecraftSessionService p_i46523_6, GameProfileRepository p_i46523_7, PlayerProfileCache p_i46523_8, CallbackInfo ci) {
        this.lan_properties$propertiesPath = this.getDataDirectory().toPath().resolve("saves").resolve(this.getFolderName()).resolve("server.properties");
        this.lan_properties$settings = new PropertyManager(this.lan_properties$propertiesPath.toFile());
        this.lan_properties$settings.saveProperties();
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void onInitServer(CallbackInfoReturnable<Boolean> cir) {
        this.lan_properties$updateSettings();
    }

    @Unique
    private void lan_properties$updateSettings() {
        this.setOnlineMode(this.lan_properties$settings.getBooleanProperty("online-mode", true));
        this.setCanSpawnAnimals(this.lan_properties$settings.getBooleanProperty("spawn-animals", true));
        this.setCanSpawnNPCs(this.lan_properties$settings.getBooleanProperty("spawn-npcs", true));
        this.setAllowPvp(this.lan_properties$settings.getBooleanProperty("pvp", true));
        this.setAllowFlight(this.lan_properties$settings.getBooleanProperty("allow-flight", false));
        this.setResourcePack(this.lan_properties$settings.getStringProperty("resource-pack", ""), this.lan_properties$loadResourcePackSHA());
        this.setMOTD(this.lan_properties$settings.getStringProperty("motd", "A Minecraft Server"));
        this.setPlayerIdleTimeout(this.lan_properties$settings.getIntProperty("player-idle-timeout", 0));
        if (this.lan_properties$settings.getIntProperty("difficulty", 1) < 0) {
            this.lan_properties$settings.setProperty("difficulty", 0);
        } else if (this.lan_properties$settings.getIntProperty("difficulty", 1) > 3) {
            this.lan_properties$settings.setProperty("difficulty", 3);
        }
        this.setBuildLimit(this.lan_properties$settings.getIntProperty("max-build-height", 256));
        this.setBuildLimit((this.getBuildLimit() + 8) / 16 * 16);
        this.setBuildLimit(MathHelper.clamp(this.getBuildLimit(), 64, 256));
        if (this.lan_properties$settings.hasProperty("announce-player-achievements")) {
            this.worlds[0].getGameRules().setOrCreateGameRule("announceAdvancements", this.lan_properties$settings.getBooleanProperty("announce-player-achievements", true) ? "true" : "false");
            this.lan_properties$settings.removeProperty("announce-player-achievements");
            this.lan_properties$settings.saveProperties();
        }
        
        this.lan_properties$loadResourcePackSHA();
    }

    @Unique
    public String lan_properties$loadResourcePackSHA() {
        if (this.lan_properties$settings.hasProperty("resource-pack-hash")) {
            if (this.lan_properties$settings.hasProperty("resource-pack-sha1")) {
                LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            } else {
                LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
                this.lan_properties$settings.getStringProperty("resource-pack-sha1", this.lan_properties$settings.getStringProperty("resource-pack-hash", ""));
                this.lan_properties$settings.removeProperty("resource-pack-hash");
            }
        }

        String string = this.lan_properties$settings.getStringProperty("resource-pack-sha1", "");
        if (!string.isEmpty() && !RESOURCE_PACK_SHA1_PATTERN.matcher(string).matches()) {
            LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
        }

        if (!this.lan_properties$settings.getStringProperty("resource-pack", "").isEmpty() && string.isEmpty()) {
            LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
        }

        return string;
    }

    @Inject(method = "shareToLAN", at = @At("HEAD"))
    public void onPublishServer(GameType type, boolean allowCheats, CallbackInfoReturnable<String> cir) {
        this.lan_properties$customProperties().lan_properties$serverPort(this.lan_properties$settings.lan_properties$serverPort());
        this.lan_properties$updateSettings();
    }

    public @NotNull CustomDedicatedServerProperties lan_properties$customProperties() {
        return this.lan_properties$settings;
    }

    public @NotNull PropertyManager lan_properties$settings() {
        return this.lan_properties$settings;
    }

    public @NotNull Path lan_properties$propertiesPath() {
        return this.lan_properties$propertiesPath;
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
