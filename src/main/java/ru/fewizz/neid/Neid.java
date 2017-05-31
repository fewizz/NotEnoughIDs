package ru.fewizz.neid;

import net.minecraft.block.BlockIce;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.BlockStatePaletteRegistry;
import net.minecraft.world.chunk.IBlockStatePalette;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Neid.MODID, name = Neid.NAME, version = Neid.VERSION)
public class Neid {
	public static final String MODID = "neid";
	public static final String NAME = "NotEnoughIDs";
	public static final String VERSION = "1.5.4.1";
	public static final boolean DEBUG_BLOCKS = false;
	public static final boolean DEBUG_ITEMS = false;

	@EventHandler
	public void init(FMLPreInitializationEvent event) {
		if (DEBUG_BLOCKS) {
			for (int i = 0; i < 10000; i++) {
				BlockIce block = new BlockIce();
				block.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
				block.setUnlocalizedName("" + i);
				GameRegistry.register(block, new ResourceLocation("neid:ICE" + i));
				GameRegistry.register(new ItemBlock(block), new ResourceLocation("neid:ICE" + i));
			}
		}
		
		if(DEBUG_ITEMS) {
			for (int i = 0; i < 35000; i++) {
				Item item = new ItemArrow();
				item.setCreativeTab(CreativeTabs.FOOD);
				item.setUnlocalizedName("" + i);
				GameRegistry.register(item, new ResourceLocation("neid:ARROW" + i));
			}
		}
	}
}
