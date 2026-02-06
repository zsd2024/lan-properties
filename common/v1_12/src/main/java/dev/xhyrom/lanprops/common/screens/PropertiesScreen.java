package dev.xhyrom.lanprops.common.screens;

import dev.xhyrom.lanprops.common.accessors.CustomIntegratedServer;
import dev.xhyrom.lanprops.common.manager.PropertyManager;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import org.jetbrains.annotations.Nullable;

public class PropertiesScreen extends GuiScreen {
    protected String screenTitle = "属性";

    @Nullable
    private final GuiScreen lastScreen;
    private PropertyManager serverSettings;

    private PropertiesList propertiesList;

    public PropertiesScreen(@Nullable GuiScreen screen) {
        this.lastScreen = screen;
    }

    @Override
    public void initGui() {
        this.screenTitle = I18n.format("lan_properties.gui.properties");

        final CustomIntegratedServer server = (CustomIntegratedServer) this.mc.getIntegratedServer();
        this.serverSettings = server.lan_properties$settings();

        this.propertiesList = new PropertiesList(this, this.mc, serverSettings);

        this.addButton(
                new GuiButton(
                        200, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("lan_properties.gui.open_world")
                )
        );

        this.addButton(
                new GuiButton(
                        201, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("gui.done")
                )
        );
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 200) {
                this.serverSettings.saveProperties();
                OpenGlHelper.openFile(((CustomIntegratedServer) this.mc.getIntegratedServer()).lan_properties$propertiesPath().getParent().toFile());
            }

            if (button.id == 201) {
                this.mc.displayGuiScreen(this.lastScreen);
            }
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        this.propertiesList.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.propertiesList.mouseClicked(mouseX, mouseY, mouseButton)) {
            return;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.propertiesList.mouseReleased(mouseX, mouseY, state)) {
            return;
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (this.propertiesList.keyTyped(typedChar, keyCode)) {
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.propertiesList.update();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        this.propertiesList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 8, 16777215);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        this.serverSettings.saveProperties();
    }
}
