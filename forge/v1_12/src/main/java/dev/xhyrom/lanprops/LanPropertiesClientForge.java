package dev.xhyrom.lanprops;

import dev.xhyrom.lanprops.common.LanPropertiesClient;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = LanPropertiesClient.MOD_ID, useMetadata = true, acceptableRemoteVersions = "*")
public class LanPropertiesClientForge {
    public LanPropertiesClientForge() {
        LanPropertiesClient.init();
    }
}
