package ru.fewizz.neid.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;
import ru.fewizz.neid.interfaces.IChunkPrimer;

@Mixin(ChunkPrimer.class)
public abstract class MixinChunkPrimer implements IChunkPrimer {
	private static final IBlockState DEFAULT_STATE2 = Blocks.AIR.getDefaultState();

	@Shadow
	char[] data = new char[65536];
	final byte[] add = new byte[16 * 16 * 256];

	@Override
	@Overwrite
	public IBlockState getBlockState(int x, int y, int z) {
		int index = getBlockIndex(x, y, z);

		int id = this.data[getBlockIndex(x, y, z)];
		id = id | (add[index] << 16);

		IBlockState iblockstate = (IBlockState) Block.BLOCK_STATE_IDS.getByValue(id);
		return iblockstate == null ? DEFAULT_STATE2 : iblockstate;
	}

	@Override
	@Overwrite
	public void setBlockState(int x, int y, int z, IBlockState state) {
		int id = Block.BLOCK_STATE_IDS.get(state);

		int index = getBlockIndex(x, y, z);

		this.data[index] = (char) (id & 0xFFFF);
		this.add[index] = (byte) (id >>> 16);
	}

	private static int getBlockIndex(int x, int y, int z) {
		return x << 12 | z << 8 | y;
	}

	@Override
	@Overwrite
	public int findGroundBlockIdx(int x, int z) {
		int i = (x << 12 | z << 8) + 256 - 1;

		for (int j = 255; j >= 0; --j) {
			IBlockState iblockstate = (IBlockState) Block.BLOCK_STATE_IDS.getByValue(this.data[i + j] | (this.add[i + j] << 16));

			if (iblockstate != null && iblockstate != DEFAULT_STATE2) {
				return j;
			}
		}

		return 0;
	}
}
