package ru.fewizz.neid.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public abstract class TransformerGroup implements Opcodes {
	int done = 0;
	final Name[] requiredClasses = getRequiredClassesInternal();
	final int needToDo = requiredClasses.length;
	
	public int getPatchedClassesCount() {
		return done;
	}
	
	public void increasePatchedClassesCount() {
		done++;
	}
	
	public boolean isPatchedAllClasses() {
		return done >= needToDo;
	}
	
	public Name[] getRequiredClasses() {
		return requiredClasses;
	}
	
	protected abstract Name[] getRequiredClassesInternal();
	
	public abstract void transform(ClassNode cn, Name clazz);
	
	public void startTransform(ClassNode cn, Name clazz) {
		transform(cn, clazz);
		increasePatchedClassesCount();
	}
}
