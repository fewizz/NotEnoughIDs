package ru.fewizz.neid.asm;

import java.lang.reflect.Field;
import java.util.Map;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class Plugin implements IFMLLoadingPlugin {
	public Plugin() {
		Field f = null;
		
		try {
			f = CoreModManager.class.getDeclaredField("deobfuscatedEnvironment");
			f.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		boolean deobf = false;
		
		try {
			deobf = f.getBoolean(null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		if(deobf) {
			MixinBootstrap.init();
			Mixins.addConfiguration("mixins.neid.json");
		}
		
		Transformer.envDeobfuscated = deobf;
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {Transformer.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		
	}

	@Override
	public String getAccessTransformerClass() {
		return AccessTransformer.class.getName();
	}
	
}
