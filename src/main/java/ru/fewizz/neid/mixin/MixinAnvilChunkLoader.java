package ru.fewizz.neid.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import ru.fewizz.neid.interfaces.IAnvilChunkLoader;
import ru.fewizz.neid.interfaces.IBlockStateContainer;

@Mixin(AnvilChunkLoader.class)
public abstract class MixinAnvilChunkLoader implements IAnvilChunkLoader {

	@Override
	@Overwrite
	public void writeChunkToNBT(Chunk chunkIn, World worldIn, NBTTagCompound compound) {
		compound.setInteger("xPos", chunkIn.xPosition);
		compound.setInteger("zPos", chunkIn.zPosition);
		compound.setLong("LastUpdate", worldIn.getTotalWorldTime());
		compound.setIntArray("HeightMap", chunkIn.getHeightMap());
		compound.setBoolean("TerrainPopulated", chunkIn.isTerrainPopulated());
		compound.setBoolean("LightPopulated", chunkIn.isLightPopulated());
		compound.setLong("InhabitedTime", chunkIn.getInhabitedTime());
		ExtendedBlockStorage[] aextendedblockstorage = chunkIn.getBlockStorageArray();
		NBTTagList nbttaglist = new NBTTagList();
		boolean flag = !worldIn.provider.getHasNoSky();

		for (ExtendedBlockStorage extendedblockstorage : aextendedblockstorage) {
			if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Y", (byte) (extendedblockstorage.getYLocation() >> 4 & 255));
				byte[] abyte = new byte[4096];
				NibbleArray nibblearray = new NibbleArray();
				NibbleArray[] nibbles = ((IBlockStateContainer) extendedblockstorage.getData()).getDataForNBT2(abyte, nibblearray);
				NibbleArray nibblearray1 = nibbles[0];
				NibbleArray nib2 = nibbles[1]; // NEID
				nbttagcompound.setByteArray("Blocks", abyte);
				nbttagcompound.setByteArray("Data", nibblearray.getData());

				if (nibblearray1 != null) {
					nbttagcompound.setByteArray("Add", nibblearray1.getData());
				}
				if (nib2 != null) {
					nbttagcompound.setByteArray("Add2", nib2.getData());
				}

				nbttagcompound.setByteArray("BlockLight", extendedblockstorage.getBlocklightArray().getData());

				if (flag) {
					nbttagcompound.setByteArray("SkyLight", extendedblockstorage.getSkylightArray().getData());
				}
				else {
					nbttagcompound.setByteArray("SkyLight", new byte[extendedblockstorage.getBlocklightArray().getData().length]);
				}

				nbttaglist.appendTag(nbttagcompound);
			}
		}

		compound.setTag("Sections", nbttaglist);
		compound.setByteArray("Biomes", chunkIn.getBiomeArray());
		chunkIn.setHasEntities(false);
		NBTTagList nbttaglist1 = new NBTTagList();

		for (int i = 0; i < chunkIn.getEntityLists().length; ++i) {
			for (Entity entity : chunkIn.getEntityLists()[i]) {
				NBTTagCompound nbttagcompound2 = new NBTTagCompound();

				if (entity.writeToNBTOptional(nbttagcompound2)) {
					try {
						chunkIn.setHasEntities(true);
						nbttaglist1.appendTag(nbttagcompound2);
					} catch (Exception e) {
						net.minecraftforge.fml.common.FMLLog.log(org.apache.logging.log4j.Level.ERROR, e, "An Entity type %s has thrown an exception trying to write state. It will not persist. Report this to the mod author", entity.getClass().getName());
					}
				}
			}
		}

		compound.setTag("Entities", nbttaglist1);
		NBTTagList nbttaglist2 = new NBTTagList();

		for (TileEntity tileentity : chunkIn.getTileEntityMap().values()) {
			try {
				NBTTagCompound nbttagcompound3 = tileentity.writeToNBT(new NBTTagCompound());
				nbttaglist2.appendTag(nbttagcompound3);
			} catch (Exception e) {
				net.minecraftforge.fml.common.FMLLog.log(org.apache.logging.log4j.Level.ERROR, e, "A TileEntity type %s has throw an exception trying to write state. It will not persist. Report this to the mod author", tileentity.getClass().getName());
			}
		}

		compound.setTag("TileEntities", nbttaglist2);
		List<NextTickListEntry> list = worldIn.getPendingBlockUpdates(chunkIn, false);

		if (list != null) {
			long j = worldIn.getTotalWorldTime();
			NBTTagList nbttaglist3 = new NBTTagList();

			for (NextTickListEntry nextticklistentry : list) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				ResourceLocation resourcelocation = (ResourceLocation) Block.REGISTRY.getNameForObject(nextticklistentry.getBlock());
				nbttagcompound1.setString("i", resourcelocation == null ? "" : resourcelocation.toString());
				nbttagcompound1.setInteger("x", nextticklistentry.position.getX());
				nbttagcompound1.setInteger("y", nextticklistentry.position.getY());
				nbttagcompound1.setInteger("z", nextticklistentry.position.getZ());
				nbttagcompound1.setInteger("t", (int) (nextticklistentry.scheduledTime - j));
				nbttagcompound1.setInteger("p", nextticklistentry.priority);
				nbttaglist3.appendTag(nbttagcompound1);
			}

			compound.setTag("TileTicks", nbttaglist3);
		}

	}

	@Override
	@Overwrite
	public Chunk readChunkFromNBT(World worldIn, NBTTagCompound compound) {
		int i = compound.getInteger("xPos");
		int j = compound.getInteger("zPos");
		Chunk chunk = new Chunk(worldIn, i, j);
		chunk.setHeightMap(compound.getIntArray("HeightMap"));
		chunk.setTerrainPopulated(compound.getBoolean("TerrainPopulated"));
		chunk.setLightPopulated(compound.getBoolean("LightPopulated"));
		chunk.setInhabitedTime(compound.getLong("InhabitedTime"));
		NBTTagList nbttaglist = compound.getTagList("Sections", 10);
		int k = 16;
		ExtendedBlockStorage[] aextendedblockstorage = new ExtendedBlockStorage[16];
		boolean flag = !worldIn.provider.getHasNoSky();

		for (int l = 0; l < nbttaglist.tagCount(); ++l) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(l);
			int i1 = nbttagcompound.getByte("Y");
			ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(i1 << 4, flag);
			byte[] abyte = nbttagcompound.getByteArray("Blocks");
			NibbleArray nibblearray = new NibbleArray(nbttagcompound.getByteArray("Data"));
			NibbleArray nibblearray1 = nbttagcompound.hasKey("Add", 7) ? new NibbleArray(nbttagcompound.getByteArray("Add")) : null;
			NibbleArray nib2 = nbttagcompound.hasKey("Add2", 7) ? new NibbleArray(nbttagcompound.getByteArray("Add2")) : null;
			((IBlockStateContainer) extendedblockstorage.getData()).setDataFromNBT2(abyte, nibblearray, nibblearray1, nib2);
			extendedblockstorage.setBlocklightArray(new NibbleArray(nbttagcompound.getByteArray("BlockLight")));

			if (flag) {
				extendedblockstorage.setSkylightArray(new NibbleArray(nbttagcompound.getByteArray("SkyLight")));
			}

			extendedblockstorage.removeInvalidBlocks();
			aextendedblockstorage[i1] = extendedblockstorage;
		}

		chunk.setStorageArrays(aextendedblockstorage);

		if (compound.hasKey("Biomes", 7)) {
			chunk.setBiomeArray(compound.getByteArray("Biomes"));
		}

		return chunk;
	}

}
