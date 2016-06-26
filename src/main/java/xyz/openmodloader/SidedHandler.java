package xyz.openmodloader;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.ITextComponent;
import xyz.openmodloader.launcher.strippable.Side;

public interface SidedHandler {
    void onInitialize();

    Side getSide();

    void openSnackbar(ITextComponent component);

	EntityPlayerSP getClientPlayer();

	void handleOnMainThread(Runnable runnable);
}
