package eutros.framedcompactdrawers;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

@SuppressWarnings("unused")
public class MixinConnector implements IMixinConnector {

    @Override
    public void connect() {
        Mixins.addConfiguration("assets/framedcompactdrawers/framedcompactdrawers.mixins.json");
    }

}
