package io.phanisment.urars.skill.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConditionInfo {
	public String key() default "";
	public String[] aliases() default {};
	public String author() default "";
}