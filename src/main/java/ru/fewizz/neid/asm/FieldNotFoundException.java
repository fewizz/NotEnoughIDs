package ru.fewizz.neid.asm;

public class FieldNotFoundException extends AsmTransformException {

	public FieldNotFoundException(String field) {
		super("can't find field " + field);
	}
}
