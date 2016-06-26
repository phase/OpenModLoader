package xyz.openmodloader.test;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;

public class BlockTest extends Block {
    public BlockTest() {
        super(Material.ROCK);
//        this.setRegistryName(new ResourceLocation("test:test"));
        this.setCreativeTab(CreativeTabs.BREWING);
        this.setUnlocalizedName("test");
    }
}
