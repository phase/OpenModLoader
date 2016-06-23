package xyz.openmodloader.event.impl;

import java.awt.image.BufferedImage;
import java.io.File;

import net.minecraft.util.text.ITextComponent;
import xyz.openmodloader.event.Event;

/**
 * An event that is fired when a screenshot is taken. This is fired after the
 * screenshot has been generated but before it has been saved. This event can be
 * canceled to prevent the screenshot from being saved.
 */
public class ScreenshotEvent extends Event {

    /**
     * The image data for the screenshot.
     */
    private final BufferedImage image;

    /**
     * The file for the screenshot. The screenshot will be saved here.
     */
    private File screenshotFile;

    /**
     * The message to send to the player when the screenshot is taken.
     */
    private ITextComponent resultMessage;

    /**
     * Constructs a new event that is fired when a screenshot is taken.
     *
     * @param image          The image data for the screenshot.
     * @param screenshotFile The file for the screenshot.
     */
    public ScreenshotEvent(BufferedImage image, File screenshotFile) {
        this.image = image;
        this.screenshotFile = screenshotFile;
    }

    /**
     * Gets the image data for the screenshot.
     *
     * @return The image data for the screenshot.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Gets the file that the screenshot will be saved to.
     *
     * @return The file that the screenshot will be saved to.
     */
    public File getScreenshotFile() {
        return screenshotFile;
    }

    /**
     * Sets the file for the screenshot to be saved to.
     *
     * @param screenshotFile The file for the screenshot to be saved to.
     */
    public void setScreenshotFile(File screenshotFile) {
        this.screenshotFile = screenshotFile;
    }

    /**
     * Gets the message to send to the player after the event has finished. A
     * message is required.
     *
     * @return The message to send to the player after the event has finished.
     */
    public ITextComponent getResultMessage() {
        return resultMessage;
    }

    /**
     * Sets the message that is sent to the player to a new one.
     *
     * @param resultMessage The new message to send to the player after the
     *                      event has finished.
     */
    public void setResultMessage(ITextComponent resultMessage) {
        this.resultMessage = resultMessage;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
