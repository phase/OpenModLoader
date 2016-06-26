package xyz.openmodloader.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.client.gui.GuiModInfo;
import xyz.openmodloader.client.gui.GuiModList;
import xyz.openmodloader.config.Config;
import xyz.openmodloader.event.impl.ArmorEvent;
import xyz.openmodloader.event.impl.BlockEvent;
import xyz.openmodloader.event.impl.CommandEvent;
import xyz.openmodloader.event.impl.EnchantmentEvent;
import xyz.openmodloader.event.impl.EntityEvent;
import xyz.openmodloader.event.impl.ExplosionEvent;
import xyz.openmodloader.event.impl.GuiEvent;
import xyz.openmodloader.event.impl.InputEvent;
import xyz.openmodloader.event.impl.MessageEvent;
import xyz.openmodloader.event.impl.ScreenshotEvent;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.modloader.IMod;

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
        OpenModLoader.INSTANCE.getEventBus().register(GuiEvent.SplashLoad.class, this::onSplashLoad);

        OpenModLoader.INSTANCE.getEventBus().register(EnchantmentEvent.Item.class, this::onItemEnchanted);
        OpenModLoader.INSTANCE.getEventBus().register(EnchantmentEvent.Level.class, this::onEnchantmentLevelCheck);

        OpenModLoader.INSTANCE.getEventBus().register(ExplosionEvent.class, this::onExplosion);

        OpenModLoader.INSTANCE.getEventBus().register(ScreenshotEvent.class, this::onScreenshot);

        OpenModLoader.INSTANCE.getEventBus().register(CommandEvent.class, this::onCommandRan);

        OpenModLoader.INSTANCE.getEventBus().register(InputEvent.Keyboard.class, this::onKeyPressed);
        OpenModLoader.INSTANCE.getEventBus().register(InputEvent.Mouse.class, this::onMouseClick);

        OpenModLoader.INSTANCE.getEventBus().register(MessageEvent.Chat.class, event -> {
            if (event.getSide() == Side.CLIENT) {
                String message = event.getMessage().getUnformattedText();
                if (message.equals(I18n.format("tile.bed.occupied")) ||
                        message.equals(I18n.format("tile.bed.noSleep")) ||
                        message.equals(I18n.format("tile.bed.notSafe")) ||
                        message.equals(I18n.format("tile.bed.notValid"))) {
                    OpenModLoader.INSTANCE.getSidedHandler().openSnackbar(event.getMessage());
                    event.setCanceled(true);
                }
            }
        });

        OpenModLoader.INSTANCE.getEventBus().register(EntityEvent.Constructing.class, this::onEntityConstruct);
        OpenModLoader.INSTANCE.getEventBus().register(EntityEvent.Join.class, this::onEntityJoinWorld);

        OpenModLoader.INSTANCE.getEventBus().register(ArmorEvent.Equip.class, this::onArmorEquip);
        OpenModLoader.INSTANCE.getEventBus().register(ArmorEvent.Unequip.class, this::onArmorUnequip);

        OpenModLoader.INSTANCE.getEventBus().register(EntityEvent.ChangeDimension.class, this::onChangeDimension);

        Config config = new Config(new File("./config/test.conf"));
        Config category1 = config.getConfig("category1", "configures stuff");
        category1.getBoolean("boolean1", true, "this is a boolean");
        category1.getBoolean("boolean2", true, "this is another boolean");
        Config category2 = category1.getConfig("category2", "configures more stuff");
        category2.getString("string1", "string", "this is a string");
        config.save();
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
        if (!(event.getGui() instanceof GuiMainMenu) && !(event.getGui() instanceof GuiModList) && !(event.getGui() instanceof GuiModInfo)) {
            Minecraft.getMinecraft().fontRendererObj.drawString("Open Mod Loader", 5, 5, 0xFFFFFFFF);
        }
    }

    private void onItemEnchanted(EnchantmentEvent.Item event) {
        OpenModLoader.INSTANCE.getLogger().info(event.getItemStack().getDisplayName() + " " + event.getEnchantments().toString());
    }

    private void onEnchantmentLevelCheck(EnchantmentEvent.Level event) {
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

    private void onSplashLoad(GuiEvent.SplashLoad event) {
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

    private void onKeyPressed(InputEvent.Keyboard event) {
        OpenModLoader.INSTANCE.getLogger().info(String.format("Key pressed %c (%d)", event.getCharacter(), event.getKey()));
    }

    private void onMouseClick(InputEvent.Mouse event) {
        OpenModLoader.INSTANCE.getLogger().info(String.format("Mouse clicked, %d", event.getButton()));
        if (event.getButton() == Keyboard.KEY_S) {
            event.setCanceled(true);
        }
    }

    private void onEntityConstruct(EntityEvent.Constructing event) {
        if (event.getEntity() instanceof EntityPlayer) {
            OpenModLoader.INSTANCE.getLogger().info("A player was constructed.");
        }
    }

    private void onEntityJoinWorld(EntityEvent.Join event) {
        if (event.getEntity() instanceof EntityPlayer) {
            OpenModLoader.INSTANCE.getLogger().info(String.format("A player joined the world on side %s.", event.getWorld().isRemote ? "client" : "server"));
        }
        if (event.getEntity() instanceof EntityPig) {
            event.setCanceled(true);
        }
    }

    private void onArmorEquip(ArmorEvent.Equip event){
        OpenModLoader.INSTANCE.getLogger().info("Entity: " + event.getEntity().getName() + " equipped " + event.getArmor().getItem().getUnlocalizedName() + " to the " + event.getSlot().getName() + " slot");
        event.setCanceled(true);
    }

    private void onArmorUnequip(ArmorEvent.Unequip event){
        OpenModLoader.INSTANCE.getLogger().info("Entity: " + event.getEntity().getName() + " unequipped " + event.getArmor().getItem().getUnlocalizedName() + " to the " + event.getSlot().getName() + " slot");
        event.setCanceled(true);
    }

    private void onChangeDimension(EntityEvent.ChangeDimension event) {
        OpenModLoader.INSTANCE.getLogger().info("Entity: " + event.getEntity().getName() + " is travelling from dimension " + event.getPreviousDimension() + " to " + event.getNewDimension());
        if (event.getNewDimension() == -1) {
            event.setNewDimension(1);
        }
    }
}
