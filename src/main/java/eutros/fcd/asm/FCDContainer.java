package eutros.fcd.asm;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

@SuppressWarnings("unused")
public class FCDContainer extends DummyModContainer {

    private static final ModMetadata meta = new ModMetadata();

    static {
        meta.version = "1.0.0";
        meta.modId = "framedcompactdrawers";
        meta.name = "Framed-Corempacting-Drawers";
        meta.authorList = ImmutableList.of("eutros");
        meta.description = "Don't mind me, just a little coremod used by Framed Compacting Drawers!";
        meta.screenshots = new String[0];
        meta.logoFile = "";
    }

    public FCDContainer() {
        super(meta);
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }

}
