package ru.fewizz.neid.asm;

public class MethodNotFoundException extends AsmTransformException {
	public MethodNotFoundException(String method) {
		super("Can't find method \"" + method + "\"");
	}
}
