package dev.xhyrom.lanprops.common.mixin;

import dev.xhyrom.lanprops.common.accessors.CustomIntegratedServer;
import dev.xhyrom.lanprops.common.screens.PropertiesScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiShareToLan;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiShareToLan.class)
public class ShareToLanScreenMixin extends GuiScreen {
    @Inject(method = "initGui", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        final CustomIntegratedServer server = (CustomIntegratedServer) this.mc.getIntegratedServer();

        assert server != null;

        this.addButton(
                new GuiButton(105, this.width / 2 - 75, 190, 150, 20, I18n.format("lan_properties.gui.properties"))
        );
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    public void onButtonPress(GuiButton button, CallbackInfo ci) {
        if (button.id == 105) {
            this.mc.displayGuiScreen(new PropertiesScreen(this));
        }
    }
}
