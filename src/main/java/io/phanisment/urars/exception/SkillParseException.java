package io.phanisment.urars.exception;

public class SkillParseException extends RuntimeException {
	public SkillParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public SkillParseException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}