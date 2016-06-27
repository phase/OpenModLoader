package xyz.openmodloader.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.client.gui.GuiModInfo;
import xyz.openmodloader.client.gui.GuiModList;
import xyz.openmodloader.config.Config;
import xyz.openmodloader.event.impl.*;
import xyz.openmodloader.event.impl.BiomeEvent.BiomeColor;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.modloader.Mod;
import xyz.openmodloader.modloader.version.UpdateManager;
import xyz.openmodloader.network.Channel;
import xyz.openmodloader.network.ChannelManager;
import xyz.openmodloader.network.DataTypes;

public class OMLTestMod implements Mod {
    private Channel channel;

    @Override
    public void onInitialize() {
        OpenModLoader.getLogger().info("Loading test mod");

        OpenModLoader.getEventBus().register(BlockEvent.Place.class, this::onBlockPlace);
        OpenModLoader.getEventBus().register(BlockEvent.Destroy.class, this::onBlockDestroy);
        OpenModLoader.getEventBus().register(BlockEvent.DigSpeed.class, this::onBlockDigSpeed);
        OpenModLoader.getEventBus().register(BlockEvent.HarvestDrops.class, this::onHarvestDrops);

        OpenModLoader.getEventBus().register(GuiEvent.Open.class, this::onGuiOpen);
        OpenModLoader.getEventBus().register(GuiEvent.Draw.class, this::onGuiDraw);
        OpenModLoader.getEventBus().register(GuiEvent.SplashLoad.class, this::onSplashLoad);

        OpenModLoader.getEventBus().register(EnchantmentEvent.Item.class, this::onItemEnchanted);
        OpenModLoader.getEventBus().register(EnchantmentEvent.Level.class, this::onEnchantmentLevelCheck);

        OpenModLoader.getEventBus().register(ExplosionEvent.class, this::onExplosion);

        OpenModLoader.getEventBus().register(ScreenshotEvent.class, this::onScreenshot);

        OpenModLoader.getEventBus().register(CommandEvent.class, this::onCommandRan);

        OpenModLoader.getEventBus().register(InputEvent.Keyboard.class, this::onKeyPressed);
        OpenModLoader.getEventBus().register(InputEvent.Mouse.class, this::onMouseClick);

        OpenModLoader.getEventBus().register(MessageEvent.Chat.class, event -> {
            if (event.getSide() == Side.CLIENT) {
                String message = event.getMessage().getUnformattedText();
                if (message.equals(I18n.format("tile.bed.occupied")) ||
                        message.equals(I18n.format("tile.bed.noSleep")) ||
                        message.equals(I18n.format("tile.bed.notSafe")) ||
                        message.equals(I18n.format("tile.bed.notValid"))) {
                    OpenModLoader.getSidedHandler().openSnackbar(event.getMessage());
                    event.setCanceled(true);
                }
            }
        });

        OpenModLoader.getEventBus().register(EntityEvent.Constructing.class, this::onEntityConstruct);
        OpenModLoader.getEventBus().register(EntityEvent.Join.class, this::onEntityJoinWorld);

        OpenModLoader.getEventBus().register(ArmorEvent.Equip.class, this::onArmorEquip);
        OpenModLoader.getEventBus().register(ArmorEvent.Unequip.class, this::onArmorUnequip);

        OpenModLoader.getEventBus().register(EntityEvent.ChangeDimension.class, this::onChangeDimension);

        OpenModLoader.getEventBus().register(EntityEvent.Mount.class, this::onMount);
        OpenModLoader.getEventBus().register(EntityEvent.Unmount.class, this::onUnmount);
        
        OpenModLoader.getEventBus().register(BiomeColor.Grass.class, this::onGrassColor);
        OpenModLoader.getEventBus().register(BiomeColor.Foliage.class, this::onFoliageColor);
        OpenModLoader.getEventBus().register(BiomeColor.Water.class, this::onWaterColor);

        OpenModLoader.getEventBus().register(EntityEvent.LightningStruck.class, this::onLightningStrike);

        OpenModLoader.getEventBus().register(PlayerEvent.Craft.class, this::onCraft);
        OpenModLoader.getEventBus().register(PlayerEvent.Smelt.class, this::onSmelt);
        OpenModLoader.getEventBus().register(PlayerEvent.ItemPickup.class, this::onPickup);

        OpenModLoader.getEventBus().register(PlayerEvent.Track.Start.class, this::onStartTracking);
        OpenModLoader.getEventBus().register(PlayerEvent.Track.Stop.class, this::onStopTracking);

        Config config = new Config(new File("./config/test.conf"));
        Config category1 = config.getConfig("category1", "configures stuff");
        category1.getBoolean("boolean1", true, "this is a boolean");
        category1.getBoolean("boolean2", true, "this is another boolean");
        Config category2 = category1.getConfig("category2", "configures more stuff");
        category2.getString("string1", "string", "this is a string");
        config.save();

        testNetwork();

        testBlock();
    }

    private void testNetwork() {
        channel = ChannelManager.create("OMLTest")
                .createPacket("test")
                    .with("str", DataTypes.STRING)
                    .handle((context, packet) -> {
                        System.out.println("PHYSICAL SIDE: " + OpenModLoader.getSidedHandler().getSide());
                        System.out.println("THREAD: " + Thread.currentThread().getName());
                        System.out.println("DATA: " + packet.get("str", DataTypes.STRING));
                    })
                .build();
    }

    private void testBlock() {
        Block.REGISTRY.register(512, new ResourceLocation("omltest:test"), new BlockTest());
    }

    private void onBlockPlace(BlockEvent.Place event) {
        OpenModLoader.getLogger().info("Placed block: " + event.getBlockState() + " isRemote: " + event.getWorld().isRemote);
        if (event.getBlockState().getBlock() == Blocks.GRASS) {
            event.setBlockState(Blocks.DIRT.getDefaultState());
        } if (event.getWorld() instanceof WorldServer && event.getBlockState().getBlock() == Blocks.BEDROCK) {
            channel.send("test")
                    .set("str", "Hello, Client!")
                    .toAllInRadius((WorldServer)event.getWorld(), event.getPos(), 8);
        }
    }

    private void onBlockDestroy(BlockEvent.Destroy event) {
        OpenModLoader.getLogger().info("Destroyed block: " + event.getBlockState() + " isRemote: " + event.getWorld().isRemote);
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
        OpenModLoader.getLogger().info("Opening gui: " + event.getGui());
        if (event.getGui() instanceof GuiLanguage) {
            event.setCanceled(true);
        } else if (event.getGui() instanceof GuiMainMenu) {
            if (!UpdateManager.getOutdatedMods().isEmpty()) {
                OpenModLoader.getSidedHandler().openSnackbar(new TextComponentString("Mod updates found!"));
            }
        }
    }

    private void onGuiDraw(GuiEvent.Draw event) {
        if (!(event.getGui() instanceof GuiMainMenu) && !(event.getGui() instanceof GuiModList) && !(event.getGui() instanceof GuiModInfo)) {
            Minecraft.getMinecraft().fontRendererObj.drawString("Open Mod Loader", 5, 5, 0xFFFFFFFF);
        }
    }

    private void onItemEnchanted(EnchantmentEvent.Item event) {
        OpenModLoader.getLogger().info(event.getItemStack().getDisplayName() + " " + event.getEnchantments().toString());
    }

    private void onEnchantmentLevelCheck(EnchantmentEvent.Level event) {
        if (event.getEnchantment() == Enchantments.FORTUNE && event.getEntityLiving().isSneaking()) {
            int oldLevel = event.getLevel();
            int newLevel = (oldLevel + 1) * 10;
            event.setLevel(newLevel);
            OpenModLoader.getLogger().info("Set fortune level from " + oldLevel + " to " + newLevel);
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
        OpenModLoader.getLogger().info("Dropping items: " + event.getDrops() + ", with fortune: " + event.getFortune() + ", with chance: " + event.getChance());
        event.getDrops().add(new ItemStack(Blocks.DIRT));
    }

    private void onCommandRan(CommandEvent event) {
        OpenModLoader.getLogger().info("Player: " + event.getSender().getName() + " ran command: " + event.getCommand().getCommandName() + " with arguments: " + Arrays.toString(event.getArgs()));
    }

    private void onKeyPressed(InputEvent.Keyboard event) {
        OpenModLoader.getLogger().info(String.format("Key pressed %c (%d)", event.getCharacter(), event.getKey()));

        if (event.getKey() == Keyboard.KEY_SEMICOLON) {
            channel.send("test")
                    .set("str", "Hello, Server!")
                    .toServer();
        }
    }

    private void onMouseClick(InputEvent.Mouse event) {
        OpenModLoader.getLogger().info(String.format("Mouse clicked, %d", event.getButton()));
        if (event.getButton() == Keyboard.KEY_S) {
            event.setCanceled(true);
        }
    }

    private void onEntityConstruct(EntityEvent.Constructing event) {
        if (event.getEntity() instanceof EntityPlayer) {
            OpenModLoader.getLogger().info("A player was constructed.");
        }
    }

    private void onEntityJoinWorld(EntityEvent.Join event) {
        if (event.getEntity() instanceof EntityPlayer) {
            OpenModLoader.getLogger().info(String.format("A player joined the world on side %s.", event.getWorld().isRemote ? "client" : "server"));
        }
        if (event.getEntity() instanceof EntityPig) {
            event.setCanceled(true);
        }
    }

    private void onArmorEquip(ArmorEvent.Equip event){
        OpenModLoader.getLogger().info("Entity: " + event.getEntity().getName() + " equipped " + Objects.toString(event.getArmor()) + " to the " + event.getSlot().getName() + " slot");
        event.setCanceled(true);
    }

    private void onArmorUnequip(ArmorEvent.Unequip event){
        OpenModLoader.getLogger().info("Entity: " + event.getEntity().getName() + " unequipped " + Objects.toString(event.getArmor()) + " to the " + event.getSlot().getName() + " slot");
        event.setCanceled(true);
    }

    private void onChangeDimension(EntityEvent.ChangeDimension event) {
        OpenModLoader.getLogger().info("Entity: %s is travelling from dimension %d to %d", event.getEntity(), event.getPreviousDimension(), event.getNewDimension());
        if (event.getNewDimension() == -1) {
            event.setNewDimension(1);
        }
    }

    private void onMount(EntityEvent.Mount event) {
        if (event.getRiding() instanceof EntityPig) {
            event.setCanceled(true);
        }
    }

    private void onUnmount(EntityEvent.Unmount event) {
        if (event.getRiding() instanceof EntityHorse) {
            event.setCanceled(true);
        }
    }
    
    private void onGrassColor(BiomeColor.Grass event) {
        if(event.getBiome() == Biomes.FOREST) {
            event.setColorModifier(Color.RED.getRGB());
        }
    }
    
    private void onFoliageColor(BiomeColor.Foliage event) {
        if(event.getBiome() == Biomes.FOREST) {
            event.setColorModifier(Color.RED.getRGB());
        }
    }
    
    private void onWaterColor(BiomeColor.Water event) {
        if(event.getBiome() == Biomes.FOREST) {
            event.setColorModifier(Color.RED.getRGB());
        }
    }

    private void onLightningStrike(EntityEvent.LightningStruck event) {
        if (event.getEntity() instanceof EntityCreeper) {
            event.setCanceled(true);
        }
    }

    private void onCraft(PlayerEvent.Craft event) {
        OpenModLoader.getLogger().info(event.getPlayer().getName() + " crafted " + event.getResult());
    }

    private void onSmelt(PlayerEvent.Smelt event) {
        OpenModLoader.getLogger().info(event.getPlayer().getName() + " smelted " + event.getResult());
        if (event.getResult().getItem() == Items.IRON_INGOT) {
            event.setXP(1.0F);
        }
    }

    private void onPickup(EntityEvent.ItemPickup event) {
        if (event.getItem().getEntityItem().getItem() == Items.APPLE) {
            event.setCanceled(true);
        }
    }

    private void onStartTracking(PlayerEvent.Track.Start event) {
        OpenModLoader.getLogger().info(event.getPlayer().getName() + " started tracking " + event.getTracking());
    }

    private void onStopTracking(PlayerEvent.Track.Stop event) {
        OpenModLoader.getLogger().info(event.getPlayer().getName() + " stopped tracking " + event.getTracking());
    }
}
