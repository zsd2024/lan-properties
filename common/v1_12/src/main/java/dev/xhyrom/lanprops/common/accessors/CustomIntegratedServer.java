package dev.xhyrom.lanprops.common.accessors;

import dev.xhyrom.lanprops.common.manager.PropertyManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface CustomIntegratedServer {
    @NotNull Path lan_properties$propertiesPath();

    @NotNull PropertyManager lan_properties$settings();

    @NotNull CustomDedicatedServerProperties lan_properties$customProperties();
}
