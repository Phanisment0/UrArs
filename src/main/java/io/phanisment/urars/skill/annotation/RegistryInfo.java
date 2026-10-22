package io.phanisment.urars.skill.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegistryInfo {
	String author(); // Will be used as namespace if namespace is empty
	String key();
	String namespace() default "";
	String[] aliases() default {};

	// --- Unnecessary info, used only for code documentation --- //
	String description() default "";
	String version() default "";
	boolean empty_args() default false; // ex: key{<value>}
}