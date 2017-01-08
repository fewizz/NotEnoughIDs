package ru.fewizz.neid.asm;

public class AsmTransformException extends RuntimeException {
	public AsmTransformException(String message) {
		super(message);
	}

	public AsmTransformException(Throwable cause) {
		super(cause);
	}

	public AsmTransformException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 5128914670008752449L;
}
