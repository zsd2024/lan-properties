package dev.xhyrom.lanprops.common.mixin;

import dev.xhyrom.lanprops.common.accessors.CustomIntegratedServer;
import dev.xhyrom.lanprops.common.hybrid.HybridMode;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(EntityPlayer.class)
public class UUDUtilMixin {
    @Inject(method = "getOfflineUUID", at = @At("HEAD"), cancellable = true)
    private static void createOfflinePlayerUUID(String name, CallbackInfoReturnable<UUID> cir) {
        if (Minecraft.getMinecraft().getIntegratedServer() == null)
            return;

        if (!((CustomIntegratedServer) Minecraft.getMinecraft().getIntegratedServer()).lan_properties$customProperties().lan_properties$hybridMode())
            return;

        final UUID uuid = HybridMode.onlinePlayerUuid(name);
        if (uuid != null) {
            cir.setReturnValue(uuid);
            cir.cancel();
        }
    }
}
