package ru.fewizz.neid.asm;

import java.io.IOException;

public class AccessTransformer extends net.minecraftforge.fml.common.asm.transformers.AccessTransformer {

	public AccessTransformer() throws IOException {
		super("META-INF/neid_at.cfg");
	}

}
