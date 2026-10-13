package io.phanisment.urars.exception;

import java.text.ParseException;

public class MathParserException extends ParseException {
	public MathParserException(String msg) {
		super(msg, -1);
	}

	public MathParserException(String msg, int pos) {
		super(msg, pos);
	}
}
