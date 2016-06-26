package xyz.openmodloader.server;

import net.minecraft.util.text.ITextComponent;
import xyz.openmodloader.ISidedHandler;
import xyz.openmodloader.launcher.strippable.Side;

public enum OMLServerHandler implements ISidedHandler {
    INSTANCE;

    @Override
    public void onInitialize() {

    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void openSnackbar(ITextComponent component) {
        //send packet
    }
}
