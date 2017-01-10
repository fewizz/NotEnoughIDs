package ru.fewizz.neid.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public abstract class TransformerGroup implements Opcodes {
	public abstract Name[] getRequiredClasses();
	public abstract void transform(ClassNode cn, Name clazz);
}
