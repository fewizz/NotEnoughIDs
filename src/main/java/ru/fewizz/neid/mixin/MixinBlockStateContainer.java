package ru.fewizz.neid.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BitArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.BlockStatePaletteHashMap;
import net.minecraft.world.chunk.BlockStatePaletteLinear;
import net.minecraft.world.chunk.IBlockStatePalette;
import net.minecraft.world.chunk.IBlockStatePaletteResizer;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.fml.common.SaveInspectionHandler;
import ru.fewizz.neid.Neid;
import ru.fewizz.neid.interfaces.IBlockStateContainer;

@Mixin(BlockStateContainer.class)
public abstract class MixinBlockStateContainer implements IBlockStatePaletteResizer, IBlockStateContainer {
	@Shadow
	static IBlockState AIR_BLOCK_STATE;
	@Shadow
	BitArray storage;
	@Shadow
	IBlockStatePalette palette;

	NibbleArray b16;

	@Nullable
	public NibbleArray[] getDataForNBT2(byte[] ids, NibbleArray metaNib) {
		NibbleArray add = null;
		NibbleArray add2 = null;

		for (int block = 0; block < 4096; ++block) {
			IBlockState s = this.get(block);
			
			int x = block & 0xF;
			int y = (block >> 8) & 0xF;
			int z = (block >> 4) & 0xF;
			
			if(s == AIR_BLOCK_STATE) {
				ids[block] = 0;
				metaNib.set(x, y, z, 0);
				continue;
			}
			
			int id = Block.BLOCK_STATE_IDS.get(s);

			int in1 = (id >> 12) & 0xF;
			int in2 = (id >> 16) & 0xF;

			if (in1 != 0) {
				if (add == null) {
					add = new NibbleArray();
				}

				add.set(x, y, z, in1);
			}
			if (in2 != 0) {
				if (add2 == null) {
					add2 = new NibbleArray();
				}

				add2.set(x, y, z, in2);
			}

			ids[block] = (byte) ((id >> 4) & 0xFF);
			metaNib.set(x, y, z, id & 0xF);
		}

		return new NibbleArray[] { add, add2 };
	}

	public void setDataFromNBT2(byte[] ids, NibbleArray meta, @Nullable NibbleArray add1, @Nullable NibbleArray add2) {
		for (int block = 0; block < 4096; ++block) {
			int x = block & 0xF;
			int y = (block >> 8) & 0xF;
			int z = (block >> 4) & 0xF;
			int toAdd = add1 == null ? 0 : add1.get(x, y, z);
			if(add2 != null) {
				toAdd = (toAdd & 0xF) | (add2.get(x, y, z) << 4);
			}
			int id = (toAdd << 12) | ((ids[block] & 0xFF) << 4) | meta.get(x, y, z);
			this.set(block, id == 0 ? AIR_BLOCK_STATE : (IBlockState) Block.BLOCK_STATE_IDS.getByValue(id));
		}
	}

	@Shadow
	abstract IBlockState get(int i);

	@Shadow
	abstract void set(int id, IBlockState state);
}
