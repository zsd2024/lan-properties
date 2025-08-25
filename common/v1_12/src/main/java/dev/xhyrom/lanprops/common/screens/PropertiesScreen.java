package dev.xhyrom.lanprops.common.screens;

import dev.xhyrom.lanprops.common.accessors.CustomIntegratedServer;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import org.jetbrains.annotations.Nullable;

public class PropertiesScreen extends Screen {
    private static final Component TITLE = Component.translatable("lan_properties.gui.properties");

    @Nullable
    private final Screen lastScreen;
    private DedicatedServerSettings serverSettings;

    private PropertiesList propertiesList;

    public PropertiesScreen(@Nullable Screen screen) {
        super(TITLE);

        this.lastScreen = screen;
    }

    @Override
    protected void init() {
        final CustomIntegratedServer server = (CustomIntegratedServer) this.minecraft.getSingleplayerServer();
        this.serverSettings = server.lan_properties$settings();

        this.propertiesList = new PropertiesList(this, this.minecraft, serverSettings.getProperties());
        this.addWidget(this.propertiesList);

        this.addRenderableWidget(Button.builder(Component.translatable("lan_properties.gui.open_world"), (button) -> Util.getPlatform().openUri(server.lan_properties$propertiesPath().getParent().toUri()))
                .bounds(this.width / 2 - 155, this.height - 29, 150, 20)
                .build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose())
                .bounds(this.width / 2 - 155 + 160, this.height - 29, 150, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 16777215);

        this.propertiesList.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.serverSettings.forceSave();
        this.minecraft.setScreen(this.lastScreen);
    }
}
