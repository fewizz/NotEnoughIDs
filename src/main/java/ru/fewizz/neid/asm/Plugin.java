package ru.fewizz.neid.asm;

import java.util.Map;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class Plugin implements IFMLLoadingPlugin {
	public Plugin() {
		MixinBootstrap.init();
		MixinEnvironment.getDefaultEnvironment().addConfiguration("mixins.neid.json");
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
