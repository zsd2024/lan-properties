package dev.xhyrom.lanprops.common.screens;

import dev.xhyrom.lanprops.common.LanPropertiesClient;
import dev.xhyrom.lanprops.common.manager.PropertyManager;
import dev.xhyrom.lanprops.common.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class PropertiesList extends GuiListExtended {
    private final PropertyManager serverProperties;
    private final GuiListExtended.IGuiListEntry[] listEntries;

    public PropertiesList(PropertiesScreen screen, Minecraft minecraft, PropertyManager serverProperties) {
        super(minecraft, screen.width + 45, screen.height, 20, screen.height - 32, 20);

        this.serverProperties = serverProperties;
        this.listEntries = new GuiListExtended.IGuiListEntry[serverProperties.lan_properties$keys().size()];

        this.populateList();
    }

    private void populateList() {
        List<PropertyData> properties = serverProperties.lan_properties$keys().entrySet().stream()
                .map(entry -> new PropertyData(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(PropertyData::name))
                .collect(Collectors.toList());

        for (int i = 0; i < properties.size(); i++) {
            PropertyData data = properties.get(i);
            try {
                this.listEntries[i] = new PropertyEntry(this, data.name(), data.type());
            } catch (IllegalAccessException e) {
                LanPropertiesClient.LOGGER.error(
                        "无法为属性'{}'创建条目（索引{}）", data.name(), i, e);
            }
        }
    }

    public void unfocusAll() {
        for (GuiListExtended.IGuiListEntry entry : this.listEntries) {
            if (entry instanceof PropertyEntry) {
                ((PropertyEntry) entry).unfocusTextField();
            }
        }
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        for (GuiListExtended.IGuiListEntry entry : this.listEntries) {
            if (entry instanceof PropertyEntry) {
                if (((PropertyEntry) entry).keyTyped(typedChar, keyCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void update() {
        for (GuiListExtended.IGuiListEntry entry : this.listEntries) {
            if (entry instanceof PropertyEntry) {
                ((PropertyEntry) entry).update();
            }
        }
    }


    private static class PropertyData {
        String name;
        Class<?> type;

        PropertyData(String name, Class<?> type) {
            this.name = name;
            this.type = type;
        }

        public String name() {
            return name;
        }

        public Class<?> type() {
            return type;
        }
    }

    protected int getSize() {
        return this.listEntries.length;
    }

    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        return this.listEntries[index];
    }

    protected int getScrollBarX() {
        return super.getScrollBarX() + 15;
    }

    public int getListWidth() {
        return 360;
    }

    public class PropertyEntry implements GuiListExtended.IGuiListEntry {
        private final PropertiesList parent;
        private final Minecraft mc;
        private final String propertyName;
        private final Class<?> propertyType;

        private final GuiButton button;
        private final GuiTextField textField;

        private boolean booleanValue;
        private Enum<?>[] enumValues;
        private int currentEnumIndex;

        private final BiConsumer<Object, String> callback;
        private static final int PADDING = 160;

        public PropertyEntry(PropertiesList parent, String propertyKey, Class<?> type) throws IllegalAccessException {
            this.parent = parent;
            this.mc = Minecraft.getMinecraft();
            this.propertyName = StringUtils.kebabCaseToTitleCase(propertyKey);
            this.propertyType = type;

            final Properties properties = serverProperties.lan_properties$properties();
            final String currentValue = (String) properties.get(propertyKey);

            this.callback = (raw, serialized) -> {
                properties.put(propertyKey, serialized);
                LanPropertiesClient.LOGGER.info("已将属性'{}'更新为'{}'", propertyKey, serialized);
            };

            if (type == boolean.class) {
                this.booleanValue = Boolean.parseBoolean(currentValue);
                this.button = new GuiButton(0, 0, 0, 150, 20, getBooleanDisplayString());
                this.textField = null;
            } else if (type.isEnum()) {
                this.textField = null;
                this.enumValues = (Enum<?>[]) type.getEnumConstants();
                this.currentEnumIndex = 0;

                for (int i = 0; i < enumValues.length; i++) {
                    if (getEnumSerializedName(enumValues[i]).equalsIgnoreCase(currentValue)) {
                        this.currentEnumIndex = i;
                        break;
                    }
                }

                this.button = new GuiButton(0, 0, 0, 150, 20, getEnumDisplayString(enumValues[currentEnumIndex]));
            } else {
                this.button = null;
                this.textField = new GuiTextField(0, this.mc.fontRenderer, 0, 0, 148, 18);
                this.textField.setText(currentValue != null ? currentValue : "");

                updateTextColor();
            }
        }

        public void unfocusTextField() {
            if (this.textField != null) {
                this.textField.setFocused(false);
            }
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            this.mc.fontRenderer.drawString(
                    this.propertyName,
                    x,
                    y + slotHeight / 2 - this.mc.fontRenderer.FONT_HEIGHT / 2,
                    0xFFFFFF
            );

            if (this.button != null) {
                this.button.x = x + PADDING;
                this.button.y = y;
                this.button.drawButton(this.mc, mouseX, mouseY, partialTicks);
            }

            if (this.textField != null) {
                this.textField.x = x + PADDING;
                this.textField.y = y + (slotHeight - 18) / 2;
                this.textField.drawTextBox();
            }
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            if (this.button != null && this.button.mousePressed(this.mc, mouseX, mouseY)) {
                this.parent.unfocusAll();

                if (this.enumValues != null) { // Cycle Enum
                    this.currentEnumIndex = (this.currentEnumIndex + 1) % this.enumValues.length;
                    Enum<?> newValue = this.enumValues[this.currentEnumIndex];
                    this.button.displayString = getEnumDisplayString(newValue);
                    this.callback.accept(newValue, getEnumSerializedName(newValue));
                } else {
                    this.booleanValue = !this.booleanValue;
                    this.button.displayString = getBooleanDisplayString();
                    this.callback.accept(this.booleanValue, Boolean.toString(this.booleanValue));
                }
                return true;
            }

            if (this.textField != null) {
                boolean wasFocused = this.textField.isFocused();

                if (this.textField.mouseClicked(mouseX, mouseY, mouseEvent)) {
                    this.parent.unfocusAll();
                    this.textField.setFocused(true);
                    return true;
                }

                if (wasFocused && !this.textField.isFocused()) {
                    onTextFieldChanged();
                }
            }
            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            if (this.button != null) {
                this.button.mouseReleased(x, y);
            }
        }

        @Override
        public void updatePosition(int slotIndex, int x, int y, float partialTicks) {}

        public boolean keyTyped(char typedChar, int keyCode) {
            if (this.textField != null && this.textField.isFocused()) {
                if (this.textField.textboxKeyTyped(typedChar, keyCode)) {
                    onTextFieldChanged();
                }

                return true;
            }

            return false;
        }

        public void update() {
            if (this.textField != null) {
                this.textField.updateCursorCounter();
            }
        }

        private void onTextFieldChanged() {
            if (this.textField == null) return;
            String text = this.textField.getText();

            if (this.propertyType == int.class) {
                try {
                    int val = Integer.parseInt(text);
                    this.callback.accept(val, text);
                    this.textField.setTextColor(0xE0E0E0); // Normal text color
                } catch (NumberFormatException e) {
                    this.textField.setTextColor(0xFF5555); // Red for error
                }
            } else if (this.propertyType == long.class) {
                try {
                    long val = Long.parseLong(text);
                    this.callback.accept(val, text);
                    this.textField.setTextColor(0xE0E0E0);
                } catch (NumberFormatException e) {
                    this.textField.setTextColor(0xFF5555);
                }
            } else { // String
                this.callback.accept(text, text);
                this.textField.setTextColor(0xE0E0E0);
            }
        }

        private void updateTextColor() {
            if (this.textField == null) return;
            String text = this.textField.getText();
            int color = 0xE0E0E0; // Default to normal
            try {
                if (this.propertyType == int.class) {
                    Integer.parseInt(text);
                } else if (this.propertyType == long.class) {
                    Long.parseLong(text);
                }
            } catch (NumberFormatException e) {
                color = 0xFF5555; // Red for error
            }
            this.textField.setTextColor(color);
        }

        private String getBooleanDisplayString() {
            return I18n.format(this.booleanValue ? "options.on" : "options.off");
        }

        private String getEnumDisplayString(Enum<?> value) {
            if (value instanceof EnumDifficulty) {
                return I18n.format(((EnumDifficulty) value).getTranslationKey());
            }
            if (value instanceof GameType) {
                return I18n.format("gameMode." + ((GameType) value).getName());
            }
            return value.name();
        }

        private String getEnumSerializedName(Enum<?> value) {
            if (value instanceof EnumDifficulty) {
                return value.name().toLowerCase(Locale.ROOT);
            }
            if (value instanceof GameType) {
                return ((GameType) value).getName();
            }
            return value.name().toLowerCase(Locale.ROOT);
        }
    }
}