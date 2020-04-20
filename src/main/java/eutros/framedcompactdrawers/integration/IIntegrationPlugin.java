package eutros.framedcompactdrawers.integration;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IIntegrationPlugin {

    void init();

    default boolean versionCheck() {
        String pattern = versionPattern();
        if(pattern == null)
            return true;

        List<ModContainer> modList = Loader.instance().getModList();

        for(ModContainer mod : modList) {
            if(mod.getModId().equals(getModID())) {
                try {
                    VersionRange validVersions = VersionRange.createFromVersionSpec(pattern);
                    ArtifactVersion version = new DefaultArtifactVersion(mod.getVersion());
                    return validVersions.containsVersion(version);
                } catch(InvalidVersionSpecificationException e) {
                    return false;
                }
            }
        }

        return false;
    }

    @Nullable
    default String versionPattern() {
        return null;
    }

    @Nonnull
    String getModID();

    void postInit();

}
