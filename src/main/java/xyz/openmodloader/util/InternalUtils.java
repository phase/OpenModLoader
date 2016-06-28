package xyz.openmodloader.util;

import java.util.Collection;

import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.client.gui.GuiLoadError;
import xyz.openmodloader.event.impl.GuiEvent;

/**
 * <b>INTERNAL - DO NOT USE</b>
 * <br>This class may be removed or modified at any time,
 * without warning. It is temporary, and solely for internal use.
 */
public class InternalUtils {

    /**
     * Something weird is going on here. If I open the GUI from any class in the
     * modloader package, the game crashes on servers, even when it's wrapped in both
     * a side check and @Strippable. If I do it here, it works.
     */
    public static void openErrorGui(Collection<?> missingDeps, Collection<?> oudatedDeps, Collection<?> duplicates) {
        OpenModLoader.getEventBus().register(GuiEvent.Open.class, (e) -> e.setGui(new GuiLoadError(missingDeps, oudatedDeps, duplicates)));
    }
}