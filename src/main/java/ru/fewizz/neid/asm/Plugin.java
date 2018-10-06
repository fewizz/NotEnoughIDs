package ru.fewizz.neid.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.SortingIndex(value = 1001)
public class Plugin implements IFMLLoadingPlugin {
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[] { Transformer.class.getName() };
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
		Transformer.envDeobfuscated = !((Boolean) data.get("runtimeDeobfuscationEnabled"));
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
