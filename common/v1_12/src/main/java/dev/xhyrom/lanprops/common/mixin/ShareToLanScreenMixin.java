package dev.xhyrom.lanprops.common.mixin;

import dev.xhyrom.lanprops.common.accessors.CustomDedicatedServerProperties;
import dev.xhyrom.lanprops.common.accessors.CustomIntegratedServer;
import dev.xhyrom.lanprops.common.screens.PropertiesScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiShareToLan;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.net.ServerSocket;

@Mixin(GuiShareToLan.class)
public class ShareToLanScreenMixin extends GuiScreen {
    private int port;

    @Unique
    private static final ITextComponent LAN_PROPERTIES = new TextComponentTranslation("lan_properties.gui.properties");

    @Inject(method = "initGui", at = @At("HEAD"))
    public void onInit(CallbackInfo ci) {
        final CustomIntegratedServer server = (CustomIntegratedServer) this.mc.getIntegratedServer();

        assert server != null;
        final CustomDedicatedServerProperties properties = server.lan_properties$customProperties();

        if (lan_properties$isPortAvailable(properties.lan_properties$serverPort()))
            this.port = properties.lan_properties$serverPort();

        this.addButton(
                new GuiButton(105, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("lan_properties.gui.properties"))
        );
    }

    @Unique
    private static boolean lan_properties$isPortAvailable(int i) {
        if (i >= 0 && i <= 65535) {
            try {
                boolean var2;
                try (ServerSocket serverSocket = new ServerSocket(i)) {
                    var2 = serverSocket.getLocalPort() == i;
                }

                return var2;
            } catch (IOException var6) {
                return false;
            }
        } else {
            return false;
        }
    }
}
