package xyz.openmodloader.event.impl;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import xyz.openmodloader.event.Event;

/**
 * Parent class for GUI related events. All events that fall within this scope
 * should extend this class. They should also be added as an inner class however
 * that is not required.
 */
public class GuiEvent extends Event {

    /**
     * The GUI that the event was triggered for.
     */
    protected GuiScreen gui;

    /**
     * Constructor for the base GUI event. This constructor should only be
     * accessed through super calls.
     *
     * @param gui The GUI that the event was triggered for.
     */
    public GuiEvent(GuiScreen gui) {
        this.gui = gui;
    }

    /**
     * An event that is fired when a GUI is opened.
     */
    public static class Open extends GuiEvent {

        /**
         * Constructor for a new event that is fired when a GUI is opened.
         *
         * @param gui The GUI to be opened.
         */
        public Open(GuiScreen gui) {
            super(gui);
        }

        /**
         * Sets the GUI to be opened to a new one.
         *
         * @param gui The new GUI to be opened.
         */
        public void setGui(GuiScreen gui) {
            this.gui = gui;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    /**
     * An event that is fired when a GUI is initialized.
     */
    public static class Init extends GuiEvent {

        /**
         * The list of buttons in the GUI
         */
        private final List<GuiButton> buttonList;

        /**
         * Constructor for a new event that is fired when a GUI is initialized.
         *
         * @param gui The GUI to be initialized.
         * @param buttonList The list of buttons that are used by the GUI.
         */
        public Init(GuiScreen gui, List<GuiButton> buttonList) {
            super(gui);
            this.buttonList = buttonList;
        }

        /**
         * Gets the list of buttons used by the GUI.
         *
         * @return The list of buttons used by the GUI.
         */
        public List<GuiButton> getButtonList() {
            return buttonList;
        }
    }

    /**
     * An event for when a button is clicked in a GUI.
     */
    public static class ButtonClick extends GuiEvent {

        /**
         * The button that was clicked.
         */
        private final GuiButton button;

        /**
         * Constructs a new event that is fired when a button is clicked.
         *
         * @param gui The GUI that contains the button.
         * @param button The button that was clicked.
         */
        public ButtonClick(GuiScreen gui, GuiButton button) {
            super(gui);
            this.button = button;
        }

        /**
         * Gets the button that was clicked.
         *
         * @return The button that was clieck.
         */
        public GuiButton getButton() {
            return button;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    /**
     * An event that is fired when a GUI is being drawn.
     */
    public static class Draw extends GuiEvent {

        private final float partialTicks;

        /**
         * Constructs a new event that is fired when a GUI is drawn.
         *
         * @param gui The GUI being drawn.
         * @param partialTicks
         */
        public Draw(GuiScreen gui, float partialTicks) {
            super(gui);
            this.partialTicks = partialTicks;
        }

        public float getPartialTicks() {
            return partialTicks;
        }
    }

    /**
     * Fired while the main Minecraft menu splash text list is being populated.
     * Allows for new splash messages to be added and existing messages to be
     * removed or altered.
     */
    public static class SplashLoad extends GuiEvent {

        /**
         * The list of splash text messages for the main menu to use when
         * deciding a splash text.
         */
        private final List<String> splashTexts;

        /**
         * Constructs an event that is fired when the main menu splash text list
         * is being populated.
         *
         * @param splashTexts
         */
        public SplashLoad(GuiScreen gui, List<String> splashTexts) {
            super(gui);
            this.splashTexts = splashTexts;
        }

        /**
         * Gets the list of all current splash text messages. Messages added to
         * this list will have a chance to appear on the main menu as a splash
         * text message. Messages can also be removed.
         *
         * @return The list of splash text messages.
         */
        public List<String> getSplashTexts() {
            return splashTexts;
        }
    }

    public GuiScreen getGui() {
        return gui;
    }
}
