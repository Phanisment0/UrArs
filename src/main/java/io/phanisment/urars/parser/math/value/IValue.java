package io.phanisment.urars.parser.math.value;

import java.util.List;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.skill.SkillContext;

public interface IValue<T> {
	T raw();
	IValue<?> member(String name) throws InvalidMathOperationException;
	IValue<?> invoke(List<IValue<?>> args) throws InvalidMathOperationException;
}