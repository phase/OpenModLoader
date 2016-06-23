package xyz.openmodloader.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.impl.*;
import xyz.openmodloader.modloader.IMod;

public class OMLTestMod implements IMod {
    @Override
    public void onEnable() {
        OpenModLoader.INSTANCE.LOGGER.info("Loading test mod");

        OpenModLoader.INSTANCE.EVENT_BUS.register(BlockEvent.Place.class, this::onBlockPlace);
        OpenModLoader.INSTANCE.EVENT_BUS.register(BlockEvent.Destroy.class, this::onBlockDestroy);
        OpenModLoader.INSTANCE.EVENT_BUS.register(BlockEvent.DigSpeed.class, this::onBlockDigSpeed);
        OpenModLoader.INSTANCE.EVENT_BUS.register(BlockEvent.HarvestDrops.class, this::onHarvestDrops);

        OpenModLoader.INSTANCE.EVENT_BUS.register(GuiEvent.Open.class, this::onGuiOpen);

        OpenModLoader.INSTANCE.EVENT_BUS.register(ItemEnchantedEvent.class, this::onItemEnchanted);

        OpenModLoader.INSTANCE.EVENT_BUS.register(ExplosionEvent.class, this::onExplosion);

        OpenModLoader.INSTANCE.EVENT_BUS.register(SplashLoadEvent.class, this::onSplashLoad);

        OpenModLoader.INSTANCE.EVENT_BUS.register(ScreenshotEvent.class, this::onScreenshot);

        OpenModLoader.INSTANCE.EVENT_BUS.register(KeyPressEvent.class, this::onKeyPressed);
    }

    private void onBlockPlace(BlockEvent.Place event) {
        OpenModLoader.INSTANCE.LOGGER.info("Placed block: " + event.getBlockState() + " isRemote: " + event.getWorld().isRemote);
        if (event.getBlockState().getBlock() == Blocks.GRASS) {
            event.setBlockState(Blocks.DIRT.getDefaultState());
        }
    }

    private void onBlockDestroy(BlockEvent.Destroy event) {
        OpenModLoader.INSTANCE.LOGGER.info("Destroyed block: " + event.getBlockState() + " isRemote: " + event.getWorld().isRemote);
        if (event.getBlockState().getBlock() == Blocks.GRASS) {
            event.setCanceled(true);
        }
    }

    private void onBlockDigSpeed(BlockEvent.DigSpeed event) {
        if (event.getBlockState().getBlock() == Blocks.DIRT) {
            event.setDigSpeed(0.05F);
        }
    }

    private void onGuiOpen(GuiEvent.Open event) {
        OpenModLoader.INSTANCE.LOGGER.info("Opening gui: " + event.getGui());
        if (event.getGui() instanceof GuiLanguage) {
            event.setCanceled(true);
        }
    }

    private void onItemEnchanted(ItemEnchantedEvent event) {
        OpenModLoader.INSTANCE.LOGGER.info(event.getItemStack().getDisplayName() + " " + event.getEnchantments().toString());
    }

    private void onExplosion(ExplosionEvent event) {
        event.setCanceled(true);
    }

    private void onSplashLoad(SplashLoadEvent event) {
        event.getSplashTexts().clear();
        event.getSplashTexts().add("OpenModLoader Test!");
    }

    private void onScreenshot(ScreenshotEvent event) {
        event.setScreenshotFile(new File("screenshotevent/", event.getScreenshotFile().getName()));
        event.setResultMessage(new TextComponentString("Screenshot saved to " + event.getScreenshotFile().getPath()));
        final BufferedImage image = event.getImage();
        final Graphics graphics = image.createGraphics();
        graphics.setColor(Color.RED);
        graphics.setFont(new Font("Arial Black", Font.BOLD, 20));
        graphics.drawString("Open Mod Loader", 20, 40);
    }

    private void onHarvestDrops(BlockEvent.HarvestDrops event){
        OpenModLoader.INSTANCE.LOGGER.info("Dropping items: " + event.getDrops() + ", with fortune: " + event.getFortune() + ", with chance: " + event.getChance());
        event.getDrops().add(new ItemStack(Blocks.DIRT));
    }

    private void onKeyPressed(KeyPressEvent event) {
        System.out.printf("Key pressed %c (%d)\n", event.getCharPressed(), event.getKeyPressed());
    }
}
