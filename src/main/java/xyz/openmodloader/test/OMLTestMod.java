package xyz.openmodloader.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.impl.*;
import xyz.openmodloader.modloader.IMod;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class OMLTestMod implements IMod {
    @Override
    public void onEnable() {
        OpenModLoader.INSTANCE.getLogger().info("Loading test mod");

        OpenModLoader.INSTANCE.getEventBus().register(BlockEvent.Place.class, this::onBlockPlace);
        OpenModLoader.INSTANCE.getEventBus().register(BlockEvent.Destroy.class, this::onBlockDestroy);
        OpenModLoader.INSTANCE.getEventBus().register(BlockEvent.DigSpeed.class, this::onBlockDigSpeed);
        OpenModLoader.INSTANCE.getEventBus().register(BlockEvent.HarvestDrops.class, this::onHarvestDrops);

        OpenModLoader.INSTANCE.getEventBus().register(GuiEvent.Open.class, this::onGuiOpen);
        OpenModLoader.INSTANCE.getEventBus().register(GuiEvent.Draw.class, this::onGuiDraw);

        OpenModLoader.INSTANCE.getEventBus().register(EnchantmentEvent.ItemEnchanted.class, this::onItemEnchanted);
        OpenModLoader.INSTANCE.getEventBus().register(EnchantmentEvent.EnchantmentLevel.class, this::onEnchantmentLevelCheck);

        OpenModLoader.INSTANCE.getEventBus().register(ExplosionEvent.class, this::onExplosion);

        OpenModLoader.INSTANCE.getEventBus().register(SplashLoadEvent.class, this::onSplashLoad);

        OpenModLoader.INSTANCE.getEventBus().register(ScreenshotEvent.class, this::onScreenshot);

        OpenModLoader.INSTANCE.getEventBus().register(CommandEvent.class, this::onCommandRan);

        OpenModLoader.INSTANCE.getEventBus().register(KeyPressEvent.class, this::onKeyPressed);
        OpenModLoader.INSTANCE.getEventBus().register(MouseClickEvent.class, this::onMouseClick);
    }

    private void onBlockPlace(BlockEvent.Place event) {
        OpenModLoader.INSTANCE.getLogger().info("Placed block: " + event.getBlockState() + " isRemote: " + event.getWorld().isRemote);
        if (event.getBlockState().getBlock() == Blocks.GRASS) {
            event.setBlockState(Blocks.DIRT.getDefaultState());
        }
    }

    private void onBlockDestroy(BlockEvent.Destroy event) {
        OpenModLoader.INSTANCE.getLogger().info("Destroyed block: " + event.getBlockState() + " isRemote: " + event.getWorld().isRemote);
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
        OpenModLoader.INSTANCE.getLogger().info("Opening gui: " + event.getGui());
        if (event.getGui() instanceof GuiLanguage) {
            event.setCanceled(true);
        }
    }

    private void onGuiDraw(GuiEvent.Draw event) {
        if (!(event.getGui() instanceof GuiMainMenu)) {
            Minecraft.getMinecraft().fontRendererObj.drawString("Open Mod Loader", 5, 5, 0xFFFF0000);
        }
    }

    private void onItemEnchanted(EnchantmentEvent.ItemEnchanted event) {
        OpenModLoader.INSTANCE.getLogger().info(event.getItemStack().getDisplayName() + " " + event.getEnchantments().toString());
    }

    private void onEnchantmentLevelCheck(EnchantmentEvent.EnchantmentLevel event) {
        if (event.getEnchantment() == Enchantments.FORTUNE && event.getEntityLiving().isSneaking()) {
            int oldLevel = event.getLevel();
            int newLevel = (oldLevel + 1) * 10;
            event.setLevel(newLevel);
            OpenModLoader.INSTANCE.getLogger().info("Set fortune level from " + oldLevel + " to " + newLevel);
        }
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

    private void onHarvestDrops(BlockEvent.HarvestDrops event) {
        OpenModLoader.INSTANCE.getLogger().info("Dropping items: " + event.getDrops() + ", with fortune: " + event.getFortune() + ", with chance: " + event.getChance());
        event.getDrops().add(new ItemStack(Blocks.DIRT));
    }

    private void onCommandRan(CommandEvent event) {
        OpenModLoader.INSTANCE.getLogger().info("Player: " + event.getSender().getName() + " ran command: " + event.getCommand().getCommandName() + " with arguments: " + Arrays.toString(event.getArgs()));
        event.setCanceled(true);
    }

    private void onKeyPressed(KeyPressEvent event) {
        System.out.printf("Key pressed %c (%d)\n", event.getCharPressed(), event.getKeyPressed());
    }

    private void onMouseClick(MouseClickEvent event) {
        System.out.printf("Mouse clicked, %d\n", event.getButton());
    }

}
