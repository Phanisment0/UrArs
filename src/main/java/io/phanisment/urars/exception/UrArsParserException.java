package io.phanisment.urars.exception;

import java.text.ParseException;

public class UrArsParserException extends ParseException {
	private final SourceLocation location;
	private boolean info = true;

	public UrArsParserException(String message, SourceLocation location) {
		super(message, location.offset);
		this.location = location;
	}

	public void noInfo() {
		this.info = false;
	}

	public SourceLocation location() {
		return location;
	}

	public String rawMessage() {
		return super.getMessage();
	}

	@Override
	public String getMessage() {
		if (info) return super.getMessage() + " (Line " + location.line() + ", column " + location.column() + ")";
		return super.getMessage();
	}

	public record SourceLocation(int line, int column, int offset, int length) {
	}
}